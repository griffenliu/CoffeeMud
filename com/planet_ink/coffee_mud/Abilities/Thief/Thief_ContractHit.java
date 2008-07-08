package com.planet_ink.coffee_mud.Abilities.Thief;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;

/*
   Copyright 2000-2008 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class Thief_ContractHit extends ThiefSkill
{
	public String ID() { return "Thief_ContractHit"; }
	public String name(){ return "Contract Hit";}
	protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected int canTargetCode(){return Ability.CAN_MOBS;}
	public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings = {"CONTRACTHIT"};
	public String[] triggerStrings(){return triggerStrings;}
	protected boolean disregardsArmorCheck(MOB mob){return true;}
    public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_CRIMINAL; }
	public String displayText(){return "";}
	protected boolean done=false;
	protected boolean readyToHit=false;
	protected boolean hitting=false;
	protected Vector hitmen=new Vector();

	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		if((affected!=null)&&(affected instanceof MOB))
			if(msg.amISource((MOB)affected)
			&&(msg.sourceMinor()==CMMsg.TYP_DEATH))
			{
				done=true;
				unInvoke();
			}
		super.executeMsg(myHost,msg);
	}

	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(invoker()!=null))
		{
			if(super.tickDown==1)
			{
				makeLongLasting();
				readyToHit=true;
			}
			MOB mob=(MOB)affected;
			if(readyToHit&&(!hitting)
			&&(mob.location()!=null)
			&&(mob.location().domainType()==Room.DOMAIN_OUTDOORS_CITY))
			{

				hitting=true;
				int num=CMLib.dice().roll(1,3,3);
				int level=mob.envStats().level();
				if(level>(invoker.envStats().level()+(2*super.getXLEVELLevel(invoker)))) level=(invoker.envStats().level()+(2*super.getXLEVELLevel(invoker)));
				CharClass C=CMClass.getCharClass("StdCharClass");
				if(C==null) C=mob.charStats().getCurrentClass();
				for(int i=0;i<num;i++)
				{
					MOB M=CMClass.getMOB("Assassin");
					M.baseEnvStats().setLevel(level);
					M.recoverEnvStats();
					M.baseEnvStats().setArmor(C.getLevelArmor(M));
					M.baseEnvStats().setAttackAdjustment(C.getLevelAttack(M));
					M.baseEnvStats().setDamage(C.getLevelDamage(M));
					M.baseEnvStats().setRejuv(0);
					M.baseState().setMana(C.getLevelMana(M));
					M.baseState().setMovement(C.getLevelMana(M));
					M.baseState().setHitPoints((10*level)+CMLib.dice().roll(level,baseEnvStats().ability(),1));
					Behavior B=CMClass.getBehavior("Thiefness");
					B.setParms("Assassin");
					M.addBehavior(B);
					M.recoverEnvStats();
					M.recoverCharStats();
					M.recoverMaxState();
					M.text(); // this establishes his current state.
					hitmen.addElement(M);
					M.bringToLife(mob.location(),true);
					M.setVictim(mob);
					mob.setVictim(M);
					Ability A=M.fetchAbility("Thief_Hide");
					if(A!=null) A.invoke(M,M,true,0);
					A=M.fetchAbility("Thief_BackStab");
					if(A!=null) A.invoke(M,mob,false,0);
				}
			}
			else
			if(hitting)
			{
				boolean anyLeft=false;
				for(int i=0;i<hitmen.size();i++)
				{
					MOB M=(MOB)hitmen.elementAt(i);
					if((!M.amDead())
					   &&(M.location()!=null)
					   &&(CMLib.flags().isInTheGame(M,false))
					   &&(CMLib.flags().aliveAwakeMobileUnbound(M,true)))
					{
						anyLeft=true;
						M.isInCombat();
						if((((M.getVictim()!=mob))
							||(!M.location().isInhabitant(mob)))
						&&(M.fetchEffect("Thief_Assassinate")==null))
						{
							M.setVictim(null);
							Ability A=M.fetchAbility("Thief_Assassinate");
							A.setProficiency(100);
							A.invoke(M,mob,false,0);
						}
					}
				}
				if(!anyLeft)
					unInvoke();
			}
		}
		return super.tick(ticking,tickID);
	}

	public void unInvoke()
	{
		MOB M=invoker();
		MOB M2=(MOB)affected;
		super.unInvoke();
		if((M!=null)&&(M2!=null)&&(((done)||(M2.amDead()))))
		{
			if(M.location()!=null)
			{
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,"Someone steps out of the shadows and whispers something to "+M.name()+".");
				M.tell("'It is done.'");
			}
		}
		for(int i=0;i<hitmen.size();i++)
		{
			M=(MOB)hitmen.elementAt(i);
			M.destroy();
		}
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<1)
		{
			mob.tell("Who would you like to put a hit out on?");
			return false;
		}
		if(mob.location()==null) return false;
		if(mob.location().domainType()!=Room.DOMAIN_OUTDOORS_CITY)
		{
			mob.tell("You need to be on the streets to put out a hit.");
			return false;
		}
		Vector V=new Vector();
		try
		{
			for(Enumeration e=CMLib.map().rooms();e.hasMoreElements();)
			{
				Room R=(Room)e.nextElement();
				if(CMLib.flags().canAccess(mob,R))
				{
					MOB M=R.fetchInhabitant(CMParms.combine(commands,0));
					if(M!=null)
						V.addElement(M);
				}
			}
	    }catch(NoSuchElementException nse){}
		MOB target=null;
		if(V.size()>0)
			target=(MOB)V.elementAt(CMLib.dice().roll(1,V.size(),-1));
		if(target==null)
		{
			mob.tell("You've never heard of '"+CMParms.combine(commands,0)+"'.");
			return false;
		}
		if(target==mob)
		{
			mob.tell("You cannot hit yourself!");
			return false;
		}
		if(!mob.mayIFight(target))
		{
			mob.tell("You are not allowed to put out a hit on "+target.name()+".");
			return false;
		}

		int level=target.envStats().level();
		if(level>(mob.envStats().level()+(2*super.getXLEVELLevel(mob)))) level=(mob.envStats().level()+(2*super.getXLEVELLevel(mob)));
		double goldRequired=100.0*level;
		String localCurrency=CMLib.beanCounter().getCurrency(mob.location());
		if(CMLib.beanCounter().getTotalAbsoluteValue(mob,localCurrency)<goldRequired)
		{
		    String costWords=CMLib.beanCounter().nameCurrencyShort(localCurrency,goldRequired);
			mob.tell("You'll need at least "+costWords+" to put a hit out on "+target.name()+".");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.envStats().level()-(mob.envStats().level()+(2*super.getXLEVELLevel(mob)));
        if(levelDiff<0) levelDiff=0;
        levelDiff*=10;
		boolean success=proficiencyCheck(mob,-levelDiff,auto);

		CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_THIEF_ACT,CMMsg.MSG_THIEF_ACT,CMMsg.MSG_THIEF_ACT,"<S-NAME> whisper(s) to a dark figure stepping out of the shadows.  The person nods and slips away.");
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);

			CMLib.beanCounter().subtractMoney(mob,localCurrency,goldRequired);
			if(success)
				maliciousAffect(mob,target,asLevel,target.envStats().level()+10,0);
		}
		return success;
	}

}
