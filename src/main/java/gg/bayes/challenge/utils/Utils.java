package gg.bayes.challenge.utils;

import gg.bayes.challenge.service.MatchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    private static final Logger LOGGER = LogManager.getLogger(MatchService.class.getName());

    public static String readFile(String fullPath) {
        try {
            return new String(Files.readAllBytes(Paths.get(fullPath)));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return "";
    }
}
