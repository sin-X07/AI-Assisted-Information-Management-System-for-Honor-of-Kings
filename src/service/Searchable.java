package service;

import model.Player;
import model.Team;

public interface Searchable {
    Player findPlainPlayerById(String id);
    Team findTeamByName(String name);
    void displayPlayerDetails(String id); // 打印玩家，英雄，装备的级联信息
}
