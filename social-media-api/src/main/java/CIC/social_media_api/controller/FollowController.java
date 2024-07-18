package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Follow;
import CIC.social_media_api.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping
    public ResponseEntity<Follow> createFollow(@RequestBody Follow follow) {
        Follow createdFollow = followService.createFollow(follow);
        return ResponseEntity.ok(createdFollow);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Follow> getFollowById(@PathVariable Long id) {
        Follow follow = followService.getFollowById(id);
        if (follow == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(follow);
    }

    @GetMapping
    public List<Follow> getAllFollows() {
        return followService.getAllFollows();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollow(@PathVariable Long id) {
        followService.deleteFollowById(id);
        return ResponseEntity.noContent().build();
    }


}
