package gg.bayes.challenge.persistence.repository;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface CombatLogEntryRepository extends JpaRepository<CombatLogEntryEntity, Long> {

    List<CombatLogEntryEntity> findCombatLogEntryEntitiesByMatchAndType(MatchEntity match, CombatLogEntryEntity.@NotNull Type type);

    List<CombatLogEntryEntity> findCombatLogEntryEntitiesByMatchAndActorAndType(MatchEntity match, String actor, CombatLogEntryEntity.@NotNull Type type);

}
