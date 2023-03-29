package gg.bayes.challenge.rest.controller;

import gg.bayes.challenge.model.exception.InputIsNotParsableException;
import gg.bayes.challenge.model.exception.InternalServerErrorException;
import gg.bayes.challenge.model.exception.MatchNotFoundException;
import gg.bayes.challenge.model.exception.NoResultException;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItem;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/match")
@Validated
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }


    /**
     * Ingests a DOTA combat log file, parses and persists relevant events data. All events are associated with the same
     * match id.
     *
     * @param combatLog the content of the combat log file
     * @return the match id associated with the parsed events
     */
    @ApiOperation(value = "Return the match id associated with the parsed events")
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Long> ingestCombatLog(@RequestBody @NotBlank String combatLog) throws InputIsNotParsableException, InternalServerErrorException {

        return ResponseEntity.ok(matchService.ingestCombatLog(combatLog));
    }

    /**
     * Fetches the heroes and their kill counts for the given match.
     *
     * @param matchId the match identifier
     * @return a collection of heroes and their kill counts
     */
    @ApiOperation(value = "Return a collection of heroes and their kill counts")
    @GetMapping(
            path = "{matchId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<HeroKills>> getMatch(@PathVariable("matchId") Long matchId) throws MatchNotFoundException, InternalServerErrorException {

        List<HeroKills> heroKillsList = matchService.getMatch(matchId).entrySet()
                .stream()
                .map(entry -> new HeroKills(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(heroKillsList);
    }

    /**
     * For the given match, fetches the items bought by the named hero.
     *
     * @param matchId  the match identifier
     * @param heroName the hero name
     * @return a collection of items bought by the hero during the match
     */
    @ApiOperation(value = "Return a collection of items bought by the hero during the match")
    @GetMapping(
            path = "{matchId}/{heroName}/items",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<HeroItem>> getHeroItems(
            @PathVariable("matchId") Long matchId,
            @PathVariable("heroName") String heroName) throws MatchNotFoundException, NoResultException, InternalServerErrorException {

        List<HeroItem> heroItemList = matchService.getHeroItems(matchId, heroName)
                .stream()
                .map(entry -> new HeroItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(heroItemList);
    }

    /**
     * For the given match, fetches the spells cast by the named hero.
     *
     * @param matchId  the match identifier
     * @param heroName the hero name
     * @return a collection of spells cast by the hero and how many times they were cast
     */
    @ApiOperation(value = "Return a collection of spells cast by the hero and how many times they were cast")
    @GetMapping(
            path = "{matchId}/{heroName}/spells",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<HeroSpells>> getHeroSpells(
            @PathVariable("matchId") Long matchId,
            @PathVariable("heroName") String heroName) throws MatchNotFoundException, NoResultException, InternalServerErrorException {

        List<HeroSpells> heroSpellsList = matchService.getHeroSpells(matchId, heroName).entrySet()
                .stream()
                .map(entry -> new HeroSpells(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(heroSpellsList);
    }

    /**
     * For a given match, fetches damage done data for the named hero.
     *
     * @param matchId  the match identifier
     * @param heroName the hero name
     * @return a collection of "damage done" (target, number of times and total damage) elements
     */
    @ApiOperation(value = "Return a collection of damage done (target, number of times and total damage) elements")
    @GetMapping(
            path = "{matchId}/{heroName}/damage",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<HeroDamage>> getHeroDamages(
            @PathVariable("matchId") Long matchId,
            @PathVariable("heroName") String heroName) throws MatchNotFoundException, NoResultException, InternalServerErrorException {

        List<HeroDamage> heroDamageList = matchService.getHeroDamages(matchId, heroName).entrySet()
                .stream()
                .map(entry -> new HeroDamage(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(heroDamageList);
    }
}
