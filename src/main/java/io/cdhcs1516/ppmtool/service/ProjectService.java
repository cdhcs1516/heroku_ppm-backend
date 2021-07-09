package io.cdhcs1516.ppmtool.service;

import io.cdhcs1516.ppmtool.domain.Backlog;
import io.cdhcs1516.ppmtool.domain.Project;
import io.cdhcs1516.ppmtool.exceptions.ProjectIdException;
import io.cdhcs1516.ppmtool.repositories.BacklogRepository;
import io.cdhcs1516.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    public Project saveOrUpdateProject(Project project) {
        String upperIdentifier = project.getProjectIdentifier().toUpperCase();

        //Logic
        try {
            project.setProjectIdentifier(upperIdentifier);

            if (project.getId() == null) {
                Backlog backlog = new Backlog();
                backlog.setProject(project);
                backlog.setProjectIdentifier(upperIdentifier);
                project.setBacklog(backlog);
            }

            if (project.getId() != null) {
                project.setBacklog(backlogRepository.findByProjectIdentifier(upperIdentifier));
            }

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project ID " + upperIdentifier + " already exists");
        }
    }

    public Project findProjectByIdentifier(String projectId) {

        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if (project == null) {
            throw new ProjectIdException("Project ID " + projectId.toUpperCase() + " does not exist");
        }

        return projectRepository.findByProjectIdentifier(projectId.toUpperCase());
    }

    public Iterable<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public void deleteProjectByIdentifier(String projectId) {
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if (project == null) {
            throw new ProjectIdException("Project ID " + projectId.toUpperCase() + " does not exist");
        }

        projectRepository.delete(project);
    }
}
