package de.thkoeln.ccq.firemanager.course;

import java.util.List;
import java.util.UUID;

public interface CourseServiceInterface {

    Course getById(UUID courseId);

    List<Course> getPrerequisites(List<UUID> prerequisiteIds);

    boolean hasMemberAllPrerequisites(UUID memberId, UUID courseId);

}
