package com.combatdetails;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerCombatDetails {
    private int totalKills;
    private int hitsTaken;
    private int hitsDone;
    private int redHitsplatsDone;
    private int redHitsplatsTaken;
    private int damageTaken;
    private int damageDealt;
    private int timeInCombat;
    @Setter
    private int resetTimer;

    public PlayerCombatDetails(){
        totalKills = 0;
        hitsDone = 0;
        redHitsplatsDone = 0;
        hitsTaken = 0;
        redHitsplatsTaken = 0;
        damageTaken = 0;
        damageDealt = 0;
        timeInCombat = 0;
        resetTimer = 0;
    }

    public void resetPlayerCombatDetails(){
        totalKills = 0;
        hitsDone = 0;
        redHitsplatsDone = 0;
        hitsTaken = 0;
        redHitsplatsTaken = 0;
        damageTaken = 0;
        damageDealt = 0;
        timeInCombat = 0;
        resetTimer = 0;
    }

    public void playerDealtDamage(int damageAmount){
        hitsDone++;
        damageDealt += damageAmount;
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
    public double getOpponentAccuracy(){ return ((double) redHitsplatsTaken) / hitsTaken; }

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
        double combatTimeMilliseconds = (double) timeInCombat*CombatDetailsPlugin.MILLISECONDS_PER_TICK;

        return 1000*60*60*totalKills / combatTimeMilliseconds;
    }

    public boolean getInCombat() {
        if(resetTimer <= 0){
            return false;
        } else{
            return true;
        }
    }
}
