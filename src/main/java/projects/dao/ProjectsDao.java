package projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.math.BigDecimal;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectsDao extends DaoBase {
	  private static final String CATEGORY_TABLE = "category";
	  private static final String MATERIAL_TABLE = "material";
	  private static final String PROJECT_TABLE = "project";
	  private static final String PROJECT_CATEGORY_TABLE = "project_category";
	  private static final String STEP_TABLE = "step";

	  /**
	   * Insert a project row into the project table.
	   * 
	   * @param project The project object to insert.
	   * @return The Project object with the primary key.
	   * @throws DbException Thrown if an error occurs inserting the row.
	   */
	  public Project insertProject(Project project) {
	    // @formatter:off
	    String sql = ""
	        + "INSERT INTO " + PROJECT_TABLE + " "
	        + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
	        + "VALUES "
	        + "(?, ?, ?, ?, ?)";
	    // @formatter:on

	    try(Connection conn = DbConnection.getConnection()) {
	      startTransaction(conn);

	      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
	        setParameter(stmt, 1, project.getProjectName(), String.class);
	        setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
	        setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
	        setParameter(stmt, 4, project.getDifficulty(), Integer.class);
	        setParameter(stmt, 5, project.getNotes(), String.class);

	        stmt.executeUpdate();

	        Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
	        commitTransaction(conn);

	        project.setProjectId(projectId);
	        return project;
	      }
	      catch(Exception e) {
	        rollbackTransaction(conn);
	        throw new DbException(e);
	      }
	    }
	    catch(SQLException e) {
	      throw new DbException(e);
	    }
	  }

	  public List<Project> fetchAllProjects() {
		    String sql = ""
		        + "SELECT * FROM " + PROJECT_TABLE + " "
		        + "ORDER BY project_name";

		    try (Connection conn = DbConnection.getConnection()) {
		        startTransaction(conn);

		        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		            try (ResultSet rs = stmt.executeQuery()) {
		                List<Project> projects = new LinkedList<>();

		                while (rs.next()) {
		                    projects.add(extract(rs, Project.class));
		                }
		                return projects;          // Move inside try block
		            }
		        } catch (Exception e) {           // Add exception handling
		            rollbackTransaction(conn);
		            throw new DbException(e);
		        }
		    } catch (SQLException e) {            // Add SQLException handling
		        throw new DbException(e);
		    }
		}

	  public Optional<Project> fetchProjectById(Integer projectId) throws SQLException {
		    String sql = ""
		        + "SELECT * FROM " + PROJECT_TABLE + " "
		        + " WHERE project_id = ?";

		    try (Connection conn = DbConnection.getConnection()) {
		        try {
		            startTransaction(conn);
		            
		            Project project = null;

		            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		                setParameter(stmt, 1, projectId, Integer.class);

		                try (ResultSet rs = stmt.executeQuery()) {
		                    if (rs.next()) {
		                        project = extract(rs, Project.class);
		                    }
		                }
		            }

		            if (Objects.nonNull(project)) {
		                project.getMaterials().addAll(fetchMaterialsForProjects(conn, projectId));
		                project.getSteps().addAll(fetchStepsForProjects(conn, projectId));
		                project.getCategories().addAll(fetchCategoriesForProjects(conn, projectId));
		            }

		            commitTransaction(conn);
		            return Optional.ofNullable(project);
		            
		        } catch (Exception e) {
		            rollbackTransaction(conn);
		            throw new DbException(e);
		        }
		    }
	  }
	  private List<Material> fetchMaterialsForProjects(Connection conn, Integer projectId) throws SQLException {
		  String sql = ""
			        + "SELECT * FROM " + MATERIAL_TABLE + " "
			        + "WHERE project_id = ?";
		    
		    List<Material> materials = new ArrayList<>();
		    
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        setParameter(stmt, 1, projectId, Integer.class);
		        
		        try (ResultSet rs = stmt.executeQuery()) {
		            while (rs.next()) {
		                materials.add(extract(rs, Material.class));
		            }
		        }
		    }
		    
		    return materials;
		}

		private List<Step> fetchStepsForProjects(Connection conn, Integer projectId) throws SQLException {
		    String sql = ""
		        + "SELECT * FROM " + STEP_TABLE + " "
		        + "WHERE project_id = ?";
		    
		    List<Step> steps = new ArrayList<>();
		    
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        setParameter(stmt, 1, projectId, Integer.class);
		        
		        try (ResultSet rs = stmt.executeQuery()) {
		            while (rs.next()) {
		                steps.add(extract(rs, Step.class));
		            }
		        }
		    }
		    
		    return steps;
		}

		private List<Category> fetchCategoriesForProjects(Connection conn, Integer projectId) throws SQLException {
		    String sql = ""
		        + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
		        + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
		        + "WHERE project_id = ?";
		    
		    List<Category> categories = new ArrayList<>();
		    
		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        setParameter(stmt, 1, projectId, Integer.class);
		        
		        try (ResultSet rs = stmt.executeQuery()) {
		            while (rs.next()) {
		                categories.add(extract(rs, Category.class));
		            }
		        }
		    }
		    
		    return categories;
		}
}

