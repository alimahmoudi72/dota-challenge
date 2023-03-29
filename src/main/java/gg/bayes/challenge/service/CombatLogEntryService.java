package gg.bayes.challenge.service;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import gg.bayes.challenge.persistence.repository.CombatLogEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CombatLogEntryService {

    private final CombatLogEntryRepository combatLogEntryRepository;

    public CombatLogEntryService(CombatLogEntryRepository combatLogEntryRepository) {
        this.combatLogEntryRepository = combatLogEntryRepository;
    }

    public List<CombatLogEntryEntity> fetchEntries(MatchEntity matchEntity, CombatLogEntryEntity.Type type) {
        return combatLogEntryRepository.findCombatLogEntryEntitiesByMatchAndType(matchEntity, type);
    }

    public List<CombatLogEntryEntity> fetchEntries(MatchEntity matchEntity, String actor, CombatLogEntryEntity.Type type) {
        return combatLogEntryRepository.findCombatLogEntryEntitiesByMatchAndActorAndType(matchEntity, actor, type);
    }
}
