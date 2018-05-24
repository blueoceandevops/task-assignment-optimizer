package app;

import java.io.File;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import domain.Interval;
import domain.Skill;
import domain.TaskAssagnmentSolution;

import domain.Employee;
import domain.Task;

public class Main {
	public void solve(String employees, String tasks, boolean save) {
		SolverFactory<TaskAssagnmentSolution> solverFactory = SolverFactory.createFromXmlResource("solver/taskAssignmentSolverConfig.xml");
		Solver<TaskAssagnmentSolution> solver = solverFactory.buildSolver();

		TaskAssagnmentSolution unsolved = ProblemBuilder.readProblemFacts(String.format("data/employees.csv", employees), String.format("data/tasks.csv", tasks));
		
		
		
		
		printEmployees(unsolved.getEmployeeList());
		printTasks(unsolved.getTaskList());
		

		Set<Interval> gaps = new HashSet<>();
		Interval brief = new Interval(LocalTime.parse("08:00"), LocalTime.parse("08:30"));
		gaps.add(brief);
		unsolved.setGaps(gaps);

		ProblemBuilder.save(unsolved, String.format("data/emp-%s-task-%s.xml",employees,tasks));

		
		TaskAssagnmentSolution solved = solver.solve(unsolved);
		ProblemBuilder.printSolution(solved);

		ProblemBuilder.save(solved, String.format("data/emp-%s-task-%s-solved.xml",employees,tasks));
	}

	public static void main(String[] args) {
		Main main = new Main();
		boolean save = false;
		if (args.length == 3)
			save=args[2].equalsIgnoreCase("save");
		
		if (args.length >= 2)
			main.solve(args[0], args[1], save);
		else
			main.solve("3", "8", save);

	}
	
	 /**
     * Affichage dans la sortie standard de la liste des employ�s pass�e en param�tres.
     * 
     * @param employees
     *            liste d'employ�s
     */
    private static void printEmployees(final List<Employee> employees) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("--------------------------EMPLOYEES------------------------------");
        System.out.println("-----------------------------------------------------------------");
        for (final Employee employee : employees) {
            System.out.print(employee.getFullName() + " | " + employee.getStartTime() + " | " + employee.getReadyTime());
            final Set<Skill> skills = employee.getSkillSet();
            
            for (Skill skill : skills) {
            	System.out.print(" | " + skill.getCategory() + " > " + skill.getPriority());
			} 
            
            System.out.println("");
        }
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Affichage dans la sortie standard de la liste des t�ches pass�e en param�tres.
     * 
     * @param tasks
     *            liste de t�ches
     */
    private static void printTasks(final List<Task> tasks) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("-----------------------------TASKS-------------------------------");
        System.out.println("-----------------------------------------------------------------");
        for (final Task task : tasks) {
            System.out.println(task.getAisle() + " | " + task.getCategory() + " | " + task.getEffort() + " | " + task.getPriority() + " | "
                    + task.getCompletionTime() + " | " + task.getMaxParts() );
        }
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Retourne le chemin du fichier pass� en param�tres.
     * 
     * @param fileName
     *            nom du fichier
     * @return le chemin du fichier
     */
    private String getPathFile(final String fileName) {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File file = new File(classLoader.getResource(fileName).getPath());
        return file.getPath();
    }
    
    public static void mainSogeti(final String[] args) {
        final Main main = new Main();

        // On r�cup�re le chemin du fichier � lire
        final String employeeFilePath = main.getPathFile("employees.csv");
        // On r�cup�re les employ�s dans un fichier csv
        final List<Employee> employees = CsvReaderBuilder.readEmployees(employeeFilePath);
        // Affichage des employ�s
        main.printEmployees(employees);

        // On r�cup�re le chemin du fichier � lire
        final String taskFilePath = main.getPathFile("tasks.csv");
        // On r�cup�re les t�ches dans un fichier csv
        final List<Task> tasks = CsvReaderBuilder.readTasks(taskFilePath);
        // Affichage des t�ches
        main.printTasks(tasks);

    }


}
