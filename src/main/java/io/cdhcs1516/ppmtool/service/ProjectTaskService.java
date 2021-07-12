package io.cdhcs1516.ppmtool.service;

import io.cdhcs1516.ppmtool.domain.Backlog;
import io.cdhcs1516.ppmtool.domain.ProjectTask;
import io.cdhcs1516.ppmtool.exceptions.ProjectNotFoundException;
import io.cdhcs1516.ppmtool.exceptions.TaskNotFoundException;
import io.cdhcs1516.ppmtool.repositories.BacklogRepository;
import io.cdhcs1516.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        try {
            // PTs to be added to a specific project (not null) where backlog exists
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

            // set the backlog to PTs
            projectTask.setBacklog(backlog);
            // format of ProjectSequence: PROID-1 PROID-2
            Integer BacklogSequence = backlog.getPTSequence();
            // update the backlog sequence
            BacklogSequence ++;
            backlog.setPTSequence(BacklogSequence);

            // Add sequence to project task
            projectTask.setProjectSequence(projectIdentifier + "-" + BacklogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            // Initial priority when priority is null
            if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
                projectTask.setPriority(3);
            }
            // Initial status when status is null
            if (projectTask.getStatus() == null || projectTask.getStatus() == "") {
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
        } catch (Exception e) {
            // handle the exception when backlog is null, i.e., project not exist
            throw new ProjectNotFoundException("Project with ID: '" + projectIdentifier + "' does not exist");
        }

    }

    public Iterable<ProjectTask> findBacklogById(String backlog_id) {
        if (backlogRepository.findByProjectIdentifier(backlog_id) == null)
            throw new ProjectNotFoundException("Project with ID: '" + backlog_id + "' does not exist");
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlog_id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {

        // make sure we are searching on the existing backlog
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if (backlog == null)
            throw new ProjectNotFoundException("Project with ID: '" + backlog_id + "' does not exist");

        // make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if (projectTask == null)
            throw new TaskNotFoundException("Task with ID: '" + pt_id + "' does not exist");

        // make sure that the backlog/project id in the path corresponds to the right project with the project task
        if (!projectTask.getProjectIdentifier().equals(backlog_id))
            throw new TaskNotFoundException("Task with ID: '" + pt_id + "' does not exist in project with ID: '" + backlog_id + "'");

        return projectTask;
    }

    // update project task
    // find existing project task
    // replace it with new task
    // save update
    public ProjectTask updateByProjectSequence(String backlog_id, String pt_id, ProjectTask updatedTask) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    // delete project task
    public void deleteByProjectSequence(String backlog_id, String pt_id) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
        projectTaskRepository.delete(projectTask);
    }


}
