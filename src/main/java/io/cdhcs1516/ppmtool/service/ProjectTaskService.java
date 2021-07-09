package io.cdhcs1516.ppmtool.service;

import io.cdhcs1516.ppmtool.domain.Backlog;
import io.cdhcs1516.ppmtool.domain.ProjectTask;
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
    }
}
