package gg.bayes.challenge.service;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;

import java.time.Duration;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CombatLogParser {
    PURCHASE_ITEM("^\\[(.*)\\] npc_dota_hero_(.*) buys item item_(.*)$") {
        @Override
        protected CombatLogEntryEntity buildCombatLogEntry(Matcher matcher) {
            return CombatLogEntryEntity.builder()
                    .timestamp(PURCHASE_ITEM.parseTimestamp(matcher.group(1)))
                    .actor(matcher.group(2))
                    .item(matcher.group(3))
                    .type(CombatLogEntryEntity.Type.ITEM_PURCHASED)
                    .build();
        }
    },

    KILL_HERO("^\\[(.*)\\] npc_dota_hero_(.*) is killed by npc_dota_hero_(.*)") {
        @Override
        protected CombatLogEntryEntity buildCombatLogEntry(Matcher matcher) {
            return CombatLogEntryEntity.builder()
                    .timestamp(KILL_HERO.parseTimestamp(matcher.group(1)))
                    .actor(matcher.group(3))
                    .target(matcher.group(2))
                    .type(CombatLogEntryEntity.Type.HERO_KILLED)
                    .build();
        }
    },

    CAST_SPELL("^\\[(.*)\\] npc_dota_hero_(.*) casts ability (.*) \\(lvl (\\d+)\\) on (.*)$") {
        @Override
        protected CombatLogEntryEntity buildCombatLogEntry(Matcher matcher) {
            String target;
            if (matcher.group(5).startsWith("npc_dota_hero_")) {
                target = matcher.group(5).substring(14);
            } else if (matcher.group(5).startsWith("npc_dota_")) {
                target = matcher.group(5).substring(9);
            } else {
                target = matcher.group(5);
            }

            return CombatLogEntryEntity.builder()
                    .timestamp(CAST_SPELL.parseTimestamp(matcher.group(1)))
                    .actor(matcher.group(2))
                    .target(target)
                    .ability(matcher.group(3))
                    .abilityLevel(Integer.parseInt(matcher.group(4)))
                    .type(CombatLogEntryEntity.Type.SPELL_CAST)
                    .build();
        }
    },

    DAMAGE_HERO("^\\[(.*)\\] npc_dota_hero_(.*) hits npc_dota_hero_(.*) with (.*) for (\\d+) damage.*$") {
        @Override
        protected CombatLogEntryEntity buildCombatLogEntry(Matcher matcher) {
            return CombatLogEntryEntity.builder()
                    .timestamp(DAMAGE_HERO.parseTimestamp(matcher.group(1)))
                    .actor(matcher.group(2))
                    .target(matcher.group(3))
                    .damage(Integer.parseInt(matcher.group(5)))
                    .type(CombatLogEntryEntity.Type.DAMAGE_DONE)
                    .build();
        }
    };

    private final Pattern pattern;

    CombatLogParser(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    protected abstract CombatLogEntryEntity buildCombatLogEntry(Matcher matcher);

    public CombatLogEntryEntity parseCombatLog(String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.matches() ? buildCombatLogEntry(matcher) : null;
    }

    private Long parseTimestamp(String s) {
        return Duration.between(LocalTime.MIDNIGHT, LocalTime.parse(s)).toMillis();
    }
}
