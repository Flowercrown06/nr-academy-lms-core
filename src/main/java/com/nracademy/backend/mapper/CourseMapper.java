package com.nracademy.backend.mapper;

import com.nracademy.backend.dto.response.CourseDto;
import com.nracademy.backend.entity.course.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDto toDto(Course course);

}