package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;

import java.util.HashMap;
import java.util.Map;

import static data.scripts.utils.tahlan_Utils.txt;

public class tahlan_DaemonCore extends BaseHullMod {

    private static final Map<HullSize, Integer> mag = new HashMap<HullSize, Integer>();
    static {
        mag.put(HullSize.FRIGATE, 2);
        mag.put(HullSize.DESTROYER, 1);
        mag.put(HullSize.CRUISER, 0);
        mag.put(HullSize.CAPITAL_SHIP, 0);
    }

    private final String INNERLARGE = "graphics/tahlan/fx/tahlan_shellshield.png";
    private final String OUTERLARGE = "graphics/tahlan/fx/tahlan_tempshield_ring.png";

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        stats.getProjectileSpeedMult().modifyMult(id, 1.5f);
        stats.getMaxRecoilMult().modifyMult(id, 0.75f);
        stats.getRecoilDecayMult().modifyMult(id, 1.25f);
        stats.getRecoilPerShotMult().modifyMult(id, 0.75f);
        stats.getDynamic().getStat(Stats.FIGHTER_CREW_LOSS_MULT).modifyMult(id, 0f);

    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getShield() != null) {
            ship.getShield().setRadius(ship.getShieldRadiusEvenIfNoShield(), INNERLARGE, OUTERLARGE);
        }
    }

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {
        // Don't do this if we're in player fleet
        if (member.getFleetCommander().isPlayer()) {
            return;
        }

        // Now we make a new captain if we don't have an AI captain already
        if (member.getCaptain() != null) {
            if (member.getCaptain().isAICore()) {
                return;
            }
        }

        int die = MathUtils.getRandomNumberInRange(1, 5) - mag.get(member.getHullSpec().getHullSize());
        PersonAPI person; // yes, a "person"
        if (die <= 1) {
            person = Misc.getAICoreOfficerPlugin(Commodities.GAMMA_CORE).createPerson(Commodities.GAMMA_CORE, "tahlan_legioinfernalis", Misc.random);
        } else if (die == 2) {
            person = Misc.getAICoreOfficerPlugin(Commodities.BETA_CORE).createPerson(Commodities.BETA_CORE, "tahlan_legioinfernalis", Misc.random);
            member.getStats().getDynamic().getMod("individual_ship_recovery_mod").modifyFlat("tahlan_daemoncore",-100f);
        } else {
            person = Misc.getAICoreOfficerPlugin(Commodities.ALPHA_CORE).createPerson(Commodities.ALPHA_CORE, "tahlan_legioinfernalis", Misc.random);
            member.getStats().getDynamic().getMod("individual_ship_recovery_mod").modifyFlat("tahlan_daemoncore",-10000f);
        }
        member.setCaptain(person);
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + 50 + txt("%");
        if (index == 1) return "" + 25 + txt("%");
        return null;
    }


}