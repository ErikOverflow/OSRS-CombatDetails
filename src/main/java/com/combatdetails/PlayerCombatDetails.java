package com.combatdetails;

import lombok.Getter;

@Getter
public class PlayerCombatDetails {
    private int totalKills;
    private int hitsTaken;
    private int hitsDone;
    private int redHitsplatsDone;
    private int redHitsplatsTaken;
    private int damageTaken;
    private int timeInCombat;
    private int resetTimer;

    public PlayerCombatDetails(int _resetTimer){
        totalKills = 0;
        hitsDone = 0;
        redHitsplatsDone = 0;
        hitsTaken = 0;
        redHitsplatsTaken = 0;
        damageTaken = 0;
        timeInCombat = 0;
        resetTimer = _resetTimer;
    }

    public void playerDealtDamage(int damageAmount){
        hitsDone++;
        if(damageAmount>0){
            redHitsplatsDone++;
        }
    }

    public void tickTimer(){
        resetTimer--;
        timeInCombat++;
    }

    public double getPlayerAccuracy(){
        return ((double) redHitsplatsDone) / hitsDone;
    }

    public void takeDamage(int damageAmount){
        hitsTaken++;
        if(damageAmount > 0){
            redHitsplatsTaken++;
        }
        damageTaken += damageAmount;
    }

    public void enemyKilled(){
        totalKills++;
    }

    public double getKillsPerHour(){
        return ((double) totalKills) / timeInCombat;
    }

    public void setResetTimer(int t){
        resetTimer = t;
    }
}
