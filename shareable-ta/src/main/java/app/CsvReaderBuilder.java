package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Employee;
import domain.Skill;
import domain.Task;

public class CsvReaderBuilder {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(CsvReaderBuilder.class);

    @SuppressWarnings("resource")
    public static List<Employee> readEmployees(final String employeeFile) {
        final Path inputFile = Paths.get(employeeFile);

        try {
            final Stream<String> stream = Files.lines(inputFile);

            final List<String[]> categories = stream.filter((line) -> !line.isEmpty() && line.startsWith("!")).map((line) -> {
                final String[] tokens = line.split(";");
                final String[] categoryArr = new String[tokens.length - 3];

                for (int i = 3, j = 0; i < tokens.length; i++, j++) {
                    categoryArr[j] = tokens[i];
                }
                return categoryArr;
            }).collect(Collectors.toList());

            final Stream<String> stream1 = Files.lines(inputFile);

            final List<Employee> employees = stream1.filter((line) -> !line.isEmpty() && !line.startsWith("#") && !line.startsWith("!")).map((line) -> {
                final String[] tokens = line.split(";");
                if (tokens.length < 3) {
                    throw new IllegalStateException("The line (" + line + ") has less than 3 tokens.");
                }

                final Set<Skill> skills = new TreeSet<Skill>();

                for (int i = 3, j = 0; i < tokens.length && j < categories.get(0).length; i++, j++) {
                    //If the employee has the skill (ie, the skill priority is not 0 )
                	if (!"0".equals(tokens[i])) {
                        skills.add(new Skill(categories.get(0)[j], Integer.valueOf(tokens[i])));
                    }
                }
                return new Employee(tokens[0], LocalTime.parse(tokens[1]), LocalTime.parse(tokens[2]), skills);

            }).collect(Collectors.toList());

            return employees;
        } catch (final IOException e) {
            throw new IllegalStateException("Reading inputFile (" + inputFile + ") failed.", e);
        }
    }

    @SuppressWarnings("resource")
    public static List<Task> readTasks(final String taskFile) {
        final Path inputFile = Paths.get(taskFile);

        try {
            final Stream<String> stream = Files.lines(inputFile);

            final List<Task> tasks = stream.filter((line) -> !line.isEmpty() && !line.startsWith("#")).map((line) -> {
                try {
                    final String[] tokens = line.split(";", -1);
                    if (tokens.length < 4) {
                        throw new IllegalStateException("The line (" + line + ") has less than 4 tokens.");
                    }

                    Float taskDuration = null;
                    if (StringUtils.isNotBlank(tokens[3])) {
                        taskDuration = floatConverter(tokens[3]);
                    }

                    Integer priority = 0;
                    if (StringUtils.isNotBlank(tokens[4])) {
                        priority = Integer.valueOf(tokens[4]);
                    }

                    LocalTime timeConstraint = null;
                    if (StringUtils.isNotBlank(tokens[5])) {
                        timeConstraint = LocalTime.parse(tokens[5]);
                    }
                    
                    //Max number of parts the task can be divided into
                    Integer maxParts = 1;
                    if (StringUtils.isNotBlank(tokens[7])) {
                    	maxParts = Integer.valueOf(tokens[7]);
                    }

                    return new Task(tokens[0], tokens[1], taskDuration, priority, timeConstraint, maxParts);
                } catch (NumberFormatException | ParseException e) {
                    LOG.debug("NumberFormatException or ParseException ", e);
                }
                return new Task();
            }).collect(Collectors.toList());

            return tasks;
        } catch (final IOException e) {
            throw new IllegalStateException("Reading inputFile (" + inputFile + ") failed.", e);
        }
    }

    private static Float floatConverter(final String data) throws ParseException {
        try {
            final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator(',');
            final DecimalFormat df = new DecimalFormat("");
            df.setDecimalFormatSymbols(dfs);
            Number number;
            number = df.parse(data);
            return number.floatValue();
        } catch (final ParseException e) {
            LOG.error("Unparseable data: " + data, e);
            throw new ParseException("ParseException", e.getErrorOffset());
        }
    }

}
