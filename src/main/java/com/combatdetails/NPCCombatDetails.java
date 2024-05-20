package com.combatdetails;

import lombok.Getter;
import lombok.Setter;

@Getter
public class NPCCombatDetails {
    private int hitsTaken;
    private int redHitsplatsTaken;
    private int damageTaken;
    private int timeInCombat;
    @Setter
    private int resetTimer;

    public NPCCombatDetails(int _resetTimer){
        hitsTaken = 0;
        redHitsplatsTaken = 0;
        damageTaken = 0;
        timeInCombat = 0;
        resetTimer = _resetTimer;
    }

    public void tickTimer(){
        resetTimer--;
        timeInCombat++;
    }

    public void takeDamage(int damageAmount){
        damageTaken += damageAmount;
        if(damageAmount > 0){
            redHitsplatsTaken++;
        }
        hitsTaken++;
    }
}
