package io.cdhcs1516.ppmtool.service;

import io.cdhcs1516.ppmtool.domain.Backlog;
import io.cdhcs1516.ppmtool.domain.Project;
import io.cdhcs1516.ppmtool.domain.User;
import io.cdhcs1516.ppmtool.exceptions.ProjectIdException;
import io.cdhcs1516.ppmtool.exceptions.ProjectNotFoundException;
import io.cdhcs1516.ppmtool.repositories.BacklogRepository;
import io.cdhcs1516.ppmtool.repositories.ProjectRepository;
import io.cdhcs1516.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username) {
        String upperIdentifier = project.getProjectIdentifier().toUpperCase();

        // handle the case when updating the project
        if (project.getId() != null) {
            findProjectByIdentifier(upperIdentifier, username);
        }

        //Logic
        try {
            User user = userRepository.findByUsername(username);

            project.setUser(user);
            project.setProjectLeader(user.getUsername());
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

    public Project findProjectByIdentifier(String projectId, String username) {

        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if (project == null) {
            throw new ProjectIdException("Project ID " + projectId.toUpperCase() + " does not exist");
        }

        if (!project.getProjectLeader().equals(username)) {
            throw new ProjectNotFoundException("Project not found in your account");
        }

        return projectRepository.findByProjectIdentifier(projectId.toUpperCase());
    }

    public Iterable<Project> findAllProjects(String username) {
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username) {
        projectRepository.delete(findProjectByIdentifier(projectId, username));
    }
}
