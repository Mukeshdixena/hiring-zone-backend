package com.hiringzone.service;

import com.hiringzone.model.SeekerProfile;
import com.hiringzone.model.User;
import com.hiringzone.repository.SeekerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final SeekerProfileRepository repository;

    public SeekerProfile getProfile(User user) {
        return repository.findByUserId(user.getId())
                .orElseGet(() -> repository.save(SeekerProfile.builder().user(user).build()));
    }

    @Transactional
    public SeekerProfile updateProfile(SeekerProfile profileDetails, User user) {
        SeekerProfile profile = getProfile(user);
        profile.setTitle(profileDetails.getTitle());
        profile.setBio(profileDetails.getBio());
        profile.setPhone(profileDetails.getPhone());
        profile.setLocation(profileDetails.getLocation());
        profile.setPortfolioUrl(profileDetails.getPortfolioUrl());
        profile.setGithubUrl(profileDetails.getGithubUrl());
        profile.setLinkedinUrl(profileDetails.getLinkedinUrl());
        profile.setSkills(profileDetails.getSkills());

        // Handle experiences and educations if they are passed
        if (profileDetails.getExperiences() != null) {
            profile.getExperiences().clear();
            profileDetails.getExperiences().forEach(e -> {
                e.setProfile(profile);
                profile.getExperiences().add(e);
            });
        }
        if (profileDetails.getEducations() != null) {
            profile.getEducations().clear();
            profileDetails.getEducations().forEach(e -> {
                e.setProfile(profile);
                profile.getEducations().add(e);
            });
        }

        return repository.save(profile);
    }
}
