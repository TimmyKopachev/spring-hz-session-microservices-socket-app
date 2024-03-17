package org.hz.session.integration.demo.service;


import org.hz.session.integration.demo.jpa.BaseRepository;
import org.hz.session.integration.demo.model.Player;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends BaseRepository<Player> {

    Player findPlayerByUsername(String username);
}
