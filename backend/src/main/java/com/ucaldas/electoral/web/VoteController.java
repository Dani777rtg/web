package com.ucaldas.electoral.web;

import com.ucaldas.electoral.security.CurrentUser;
import com.ucaldas.electoral.service.VoteService;
import com.ucaldas.electoral.web.dto.VoteDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;
    private final CurrentUser currentUser;

    public VoteController(VoteService voteService, CurrentUser currentUser) {
        this.voteService = voteService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public Map<String, String> vote(@Valid @RequestBody VoteDtos.CastVoteRequest req) {
        voteService.castVote(currentUser.requireUserId(), req);
        return Map.of("message", "Voto registrado.");
    }
}
