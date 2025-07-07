package projects;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	  private Scanner scanner = new Scanner(System.in);
	  private ProjectService projectService = new ProjectService();
	  private Project curProject;

	 
	  private List<String> operations = List.of(
	      "1) Add a project",
	      "2) List project.",
	      "3) Select a project"
	  );
	  
	  public static void main(String[] args) {
	    new ProjectsApp().processUserSelections();
	  }
	  
	/*
	 * Obtains user's selection (i.e user selects option 1 and types 1)
	 */
	
	private void processUserSelections() {
		boolean done = false;
		
		while(!done) {
			try {
		        int selection = getUserSelection();

		        switch(selection) {
		          case -1:
		            done = exitMenu();
		            break;

		          case 1:
		            createProject();
		            break;
		            
		          case 2:
		              listProjects();  // Add this case
		              break;
		              
		          case 3:
		        	  selectProject();
		        	  break;

		          default:
		            System.out.println("\n" + selection + " is not a valid selection. Try again. \n");
		            break;
		        }
		      }
		      catch(Exception e) {
		        System.out.println("\nError: " + e + " Try again.");
		      }
		    }
		  }

	
	
	private void selectProject() throws NoSuchElementException, SQLException {
		listProjects();
		
		Integer projectId = getIntInput("Select a project ID");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
				
		}
		
	

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName() + "\n"));
		
	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
	    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
	    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
	    Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
	    String notes = getStringInput("Enter the project notes");
	    
	    Project project = new Project();
	    
	    project.setProjectName(projectName);
	    project.setEstimatedHours(estimatedHours);
	    project.setActualHours(actualHours);
	    project.setDifficulty(difficulty);
	    project.setNotes(notes);
	    
	    Project dbProject = projectService.addProject(project);
	    System.out.println("You have succesfully created a new project: " + dbProject);
	    
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

	    if(Objects.isNull(input)) {
	      return null;
	    }

	    try {
	      return new BigDecimal(input).setScale(2);
	    		  }
	    catch(NumberFormatException e) {
	      throw new DbException(input + " is not a valid decimal number.");
	    }
	  }

	private boolean exitMenu() {
	    System.out.println("Menu exited" + ".");
	    return true;
	  }
	
	private int getUserSelection() {
	    printOperations();

	    Integer input = getIntInput("Enter a menu selection");

	    return Objects.isNull(input) ? -1 : input;
	  }
	
	/*
	 * Prints menu options
	 */
	private void printOperations() {
		System.out.println("Please select a Menu option from the available options. or Press Enter to quit:");	
		operations.forEach(line -> System.out.println("  " + line));
		
		if (Objects.isNull(curProject)) {
		      System.out.println("\nYou are not working with a project.");
		    } else {
		      System.out.println("\nYou are working with project " + curProject);
		    }
	}
	
	
	/*
	 * Takes in the user input for menu selection
	 */
	private Integer getIntInput(String prompt) {
	    String input = getStringInput(prompt);

	    if(Objects.isNull(input)) {
	      return null;
	    }

	    try {
	      return Integer.valueOf(input);
	    }
	    catch(NumberFormatException e) {
	      throw new DbException(input + " is not a valid number.");
	    }
	  }

	/*
	 * Accepts input from the user and converts it to an Integer
	 */
	private String getStringInput(String prompt) {
    System.out.print(prompt + ": ");
    String input = scanner.nextLine();

    return input.isBlank() ? null : input.trim();
  }

	
	
}
