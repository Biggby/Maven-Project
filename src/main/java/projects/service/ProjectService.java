package projects.service;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectsDao;
import projects.entity.Project;

public class ProjectService {
	  private ProjectsDao projectDao = new ProjectsDao();

	  
	  public Project addProject(Project project) {
		    return projectDao.insertProject(project);
		  }

	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) throws NoSuchElementException, SQLException {
		return projectDao.fetchProjectById(projectId).orElseThrow(
				() -> new NoSuchElementException("Project with ID:" + projectId + "Does not exist"));
		
	}
}