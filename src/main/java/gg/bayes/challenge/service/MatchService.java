package gg.bayes.challenge.service;

import gg.bayes.challenge.model.exception.InputIsNotParsableException;
import gg.bayes.challenge.model.exception.InternalServerErrorException;
import gg.bayes.challenge.model.exception.MatchNotFoundException;
import gg.bayes.challenge.model.exception.NoResultException;
import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import gg.bayes.challenge.persistence.repository.MatchRepository;
import gg.bayes.challenge.utils.Utils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MatchService {

    private static final Logger LOGGER = LogManager.getLogger(MatchService.class.getName());
    private final MatchRepository matchRepository;
    private final CombatLogEntryService combatLogEntryService;

    public MatchService(MatchRepository matchRepository, CombatLogEntryService combatLogEntryService) {
        this.matchRepository = matchRepository;
        this.combatLogEntryService = combatLogEntryService;
    }

    @PostConstruct
    private void initial() {
        try (Stream<Path> paths = Files.walk(Paths.get("data/"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            ingestCombatLog(Utils.readFile(path.toString()));
                        } catch (InputIsNotParsableException | InternalServerErrorException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    });
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Fetches the mach entity.
     *
     * @param matchId the match identifier
     * @return the match entity associated with match id
     */
    private MatchEntity findMatch(Long matchId) throws MatchNotFoundException {
        Optional<MatchEntity> match = matchRepository.findById(matchId);
        if (match.isPresent()) {
            return match.get();
        } else {
            throw new MatchNotFoundException();
        }
    }

    /**
     * Ingests a DOTA combat log file, parses and persists relevant events data. All events are associated with the same
     * match id.
     *
     * @param combatLog the content of the combat log file
     * @return the match id associated with the parsed events
     */
    public Long ingestCombatLog(String combatLog) throws InputIsNotParsableException, InternalServerErrorException {
        try {
            MatchEntity matchEntity = new MatchEntity();
            combatLog
                    .lines()
                    .forEach(line -> Arrays.stream(CombatLogParser.values())
                            .map(parser -> parser.parseCombatLog(line))
                            .filter(Objects::nonNull)
                            .findFirst()
                            .ifPresent(matchEntity::addCombatLogEntry));
            if (matchEntity.getCombatLogEntries().size() == 0) {
                throw new InputIsNotParsableException();
            }
            return matchRepository.save(matchEntity).getId();
        } catch (InputIsNotParsableException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalServerErrorException();
        }
    }

    /**
     * Fetches the heroes and their kill counts for the given match.
     *
     * @param matchId the match identifier
     * @return a Map which its key refers to hero name and its value refers to his kill count
     */
    public Map<String, Long> getMatch(Long matchId) throws MatchNotFoundException, InternalServerErrorException {
        try {
            return combatLogEntryService.fetchEntries(findMatch(matchId), CombatLogEntryEntity.Type.HERO_KILLED)
                    .stream()
                    .collect(Collectors.groupingBy(CombatLogEntryEntity::getActor, Collectors.counting()));
        } catch (MatchNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalServerErrorException();
        }
    }

    /**
     * For the given match, fetches the items bought by the named hero.
     *
     * @param matchId the match identifier
     * @param actor   the hero name
     * @return a List of pairs containing items bought by the hero and the timestamp respectively
     */
    public List<Pair<String, Long>> getHeroItems(Long matchId, String actor) throws MatchNotFoundException,
            NoResultException, InternalServerErrorException {
        try {
            List<Pair<String, Long>> result = new ArrayList<>();
            combatLogEntryService.fetchEntries(findMatch(matchId), actor, CombatLogEntryEntity.Type.ITEM_PURCHASED)
                    .forEach(entry -> result.add(new ImmutablePair<>(entry.getItem(), entry.getTimestamp())));
            if (result.size() == 0) {
                throw new NoResultException();
            }
            return result;
        } catch (MatchNotFoundException | NoResultException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalServerErrorException();
        }
    }

    /**
     * For the given match, fetches the spells cast by the named hero.
     *
     * @param matchId the match identifier
     * @param actor   the hero name
     * @return a Map which its key refers to spells cast by the hero and its value refers to how many times they were cast
     */
    public Map<String, Long> getHeroSpells(Long matchId, String actor) throws MatchNotFoundException, NoResultException,
            InternalServerErrorException {
        try {
            Map<String, Long> result = combatLogEntryService.fetchEntries(findMatch(matchId), actor, CombatLogEntryEntity.Type.SPELL_CAST)
                    .stream()
                    .collect(Collectors.groupingBy(CombatLogEntryEntity::getAbility, Collectors.counting()));
            if (result.size() == 0) {
                throw new NoResultException();
            }
            return result;
        } catch (MatchNotFoundException | NoResultException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalServerErrorException();
        }
    }

    /**
     * For the given match, fetches damage done data for the named hero.
     *
     * @param matchId the match identifier
     * @param actor   the hero name
     * @return a Map which its key refers to target and its value refers to a Pair containing number of times and total
     * damage elements respectively
     */
    public Map<String, Pair<Integer, Integer>> getHeroDamages(Long matchId, String actor) throws MatchNotFoundException,
            NoResultException, InternalServerErrorException {
        try {
            Map<String, List<CombatLogEntryEntity>> map =
                    combatLogEntryService.fetchEntries(findMatch(matchId), actor, CombatLogEntryEntity.Type.DAMAGE_DONE)
                            .stream()
                            .collect(Collectors.groupingBy(CombatLogEntryEntity::getTarget));
            Map<String, Pair<Integer, Integer>> result = new HashMap<>();
            map.forEach((key, value) -> result.put(key, new ImmutablePair<>(value.size(), value.stream().mapToInt(CombatLogEntryEntity::getDamage).sum())));
            if (result.size() == 0) {
                throw new NoResultException();
            }
            return result;
        } catch (MatchNotFoundException | NoResultException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalServerErrorException();
        }
    }
}
