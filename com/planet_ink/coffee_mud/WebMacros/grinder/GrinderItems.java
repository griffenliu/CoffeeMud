package com.planet_ink.coffee_mud.web.macros.grinder;
import java.util.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.web.macros.RoomData;

/* 
   Copyright 2000-2005 Bo Zimmerman

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
public class GrinderItems
{
	public static String editItem(ExternalHTTPRequests httpReq,
								  Hashtable parms,
								  Room R)
	{
		String itemCode=httpReq.getRequestParameter("ITEM");
		if(itemCode==null) return "@break@";

		String mobNum=httpReq.getRequestParameter("MOB");
		String newClassID=httpReq.getRequestParameter("CLASSES");

		CoffeeUtensils.resetRoom(R);

		Item I=null;
		MOB M=null;
		if((mobNum!=null)&&(mobNum.length()>0))
		{
			M=RoomData.getMOBFromCode(R,mobNum);
			if(M==null)
			{
				StringBuffer str=new StringBuffer("No MOB?!");
				str.append(" Got: "+mobNum);
				str.append(", Includes: ");
				for(int m=0;m<R.numInhabitants();m++)
				{
					MOB M2=R.fetchInhabitant(m);
					if((M2!=null)&&(M2.isEligibleMonster()))
					   str.append(M2.Name()+"="+RoomData.getMOBCode(R,M2));
				}
				return str.toString();
			}
		}
		if(itemCode.equals("NEW"))
			I=CMClass.getItem(newClassID);
		else
		if(M!=null)
			I=RoomData.getItemFromCode(M,itemCode);
		else
			I=RoomData.getItemFromCode(R,itemCode);

		if(I==null)
		{
			StringBuffer str=new StringBuffer("No Item?!");
			str.append(" Got: "+itemCode);
			str.append(", Includes: ");
			if(M==null)
				for(int i=0;i<R.numItems();i++)
				{
					Item I2=R.fetchItem(i);
					if(I2!=null) str.append(I2.Name()+"="+RoomData.getItemCode(R,I2));
				}
			else
				for(int i=0;i<M.inventorySize();i++)
				{
					Item I2=M.fetchInventory(i);
					if(I2!=null) str.append(I2.Name()+"="+RoomData.getItemCode(M,I2));
				}
			return str.toString();
		}
		Item oldI=I;
		if((newClassID!=null)&&(!newClassID.equals(CMClass.className(I))))
			I=CMClass.getItem(newClassID);

		String[] okparms={"NAME","CLASSES","DISPLAYTEXT","DESCRIPTION"," LEVEL",
		        		  " ABILITY"," REJUV"," MISCTEXT","MATERIALS","ISGENERIC",
		        		  "ISREADABLE","READABLETEXT","ISDRINK","LIQUIDHELD","QUENCHED",
		        		  "ISCONTAINER","CAPACITY","ISARMOR","ARMOR","WORNDATA",
		        		  " HEIGHT","ISWEAPON","WEAPONTYPE","WEAPONCLASS","ATTACK",
		        		  "DAMAGE","MINRANGE","MAXRANGE","SECRETIDENTITY",
		        		  "ISGETTABLE","ISREMOVABLE","ISDROPPABLE","ISTWOHANDED","ISTRAPPED",
		        		  "READABLESPELLS","ISWAND"," USESREMAIN","VALUE","WEIGHT",
		        		  "ISMAP","MAPAREAS","ISFOOD","ISPILL","ISSUPERPILL",
		        		  "ISPOTION","LIQUIDTYPES","AMMOTYPE","AMMOCAP","READABLESPELL",
		        		  "ISRIDEABLE","RIDEABLETYPE","MOBSHELD","HASALID","HASALOCK",
		        		  "KEYCODE","ISWALLPAPER","NOURISHMENT","CONTAINER","ISLIGHTSOURCE",
		        		  "DURATION","NONLOCATABLE","ISKEY","CONTENTTYPES","ISINSTRUMENT",
		        		  "INSTRUMENTTYPE","ISAMMO","ISMOBITEM","ISDUST","ISPERFUME",
		        		  "SMELLS","IMAGE","ISEXIT","EXITNAME","EXITCLOSEDTEXT",
						  "NUMCOINS","CURRENCY","DENOM"
						  };
		for(int o=0;o<okparms.length;o++)
		{
			String parm=okparms[o];
			boolean generic=true;
			if(parm.startsWith(" "))
			{
				generic=false;
				parm=parm.substring(1);
			}
			String old=httpReq.getRequestParameter(parm);
			if(old==null) old="";

			if((I.isGeneric()||(!generic)))
			switch(o)
			{
			case 0: // name
				I.setName(old);
				break;
			case 1: // classes
				break;
			case 2: // displaytext
				I.setDisplayText(old);
				break;
			case 3: // description
				I.setDescription(old);
				break;
			case 4: // level
				I.baseEnvStats().setLevel(Util.s_int(old));
				break;
			case 5: // ability;
				I.baseEnvStats().setAbility(Util.s_int(old));
				break;
			case 6: // rejuv;
				I.baseEnvStats().setRejuv(Util.s_int(old));
				break;
			case 7: // misctext
				if(!I.isGeneric())
					I.setMiscText(old);
				break;
			case 8: // materials
				I.setMaterial(Util.s_int(old));
				break;
			case 9: // is generic
				break;
			case 10: // isreadable
				Sense.setReadable(I,old.equals("on"));
				break;
			case 11: // readable text
				if(!(I instanceof Ammunition))
					I.setReadableText(old);
				break;
			case 12: // is drink
				break;
			case 13: // liquid held
				if(I instanceof Drink)
				{
					((Drink)I).setLiquidHeld(Util.s_int(old));
					((Drink)I).setLiquidRemaining(Util.s_int(old));
				}
				break;
			case 14: // quenched
				if(I instanceof Drink)
					((Drink)I).setThirstQuenched(Util.s_int(old));
				break;
			case 15: // is container
				break;
			case 16: // capacity
				if(I instanceof Container)
					((Container)I).setCapacity(Util.s_int(old));
				break;
			case 17: // is armor
				break;
			case 18: // armor
				if(I instanceof Armor)
					I.baseEnvStats().setArmor(Util.s_int(old));
				break;
			case 19: // worn data
				if(((I instanceof Armor)||(I instanceof MusicalInstrument))
				&&(httpReq.isRequestParameter("WORNDATA")))
				{
					int climate=Util.s_int(httpReq.getRequestParameter("WORNDATA"));
					for(int i=1;;i++)
						if(httpReq.isRequestParameter("WORNDATA"+(new Integer(i).toString())))
							climate=climate|Util.s_int(httpReq.getRequestParameter("WORNDATA"+(new Integer(i).toString())));
						else
							break;
					I.setRawProperLocationBitmap(climate);
				}
				break;
			case 20: // height
				if(I instanceof Armor)
					I.baseEnvStats().setHeight(Util.s_int(old));
				break;
			case 21: // is weapon
				break;
			case 22: // weapon type
				if(I instanceof Weapon)
					((Weapon)I).setWeaponType(Util.s_int(old));
				break;
			case 23: // weapon class
				if(I instanceof Weapon)
					((Weapon)I).setWeaponClassification(Util.s_int(old));
				break;
			case 24: // attack
				if(I instanceof Weapon)
					I.baseEnvStats().setAttackAdjustment(Util.s_int(old));
				break;
			case 25: // damage
				if(I instanceof Weapon)
					I.baseEnvStats().setDamage(Util.s_int(old));
				break;
			case 26: // min range
				if(I instanceof Weapon)
					((Weapon)I).setRanges(Util.s_int(old),I.maxRange());
				break;
			case 27: // max range
				if(I instanceof Weapon)
					((Weapon)I).setRanges(I.minRange(),Util.s_int(old));
				break;
			case 28: // secret identity
				I.setSecretIdentity(old);
				break;
			case 29: // is gettable
				Sense.setGettable(I,old.equals("on"));
				break;
			case 30: // is removable
				Sense.setRemovable(I,old.equals("on"));
				break;
			case 31: // is droppable
				Sense.setDroppable(I,old.equals("on"));
				break;
			case 32: // is two handed
				if((I instanceof Weapon)||(I instanceof Armor))
					I.setRawLogicalAnd(old.equals("on"));
				break;
			case 33: // is trapped
				break;
			case 34: // readable spells
				if(((I instanceof SpellHolder))
				&&(CMClass.className(I).indexOf("SuperPill")<0))
				{
					if(httpReq.isRequestParameter("READABLESPELLS"))
					{
						old=";"+httpReq.getRequestParameter("READABLESPELLS");
						for(int i=1;;i++)
							if(httpReq.isRequestParameter("READABLESPELLS"+(new Integer(i).toString())))
								old+=";"+httpReq.getRequestParameter("READABLESPELLS"+(new Integer(i).toString()));
							else
								break;
					}
					old=old+";";
					((SpellHolder)I).setSpellList(old);
				}
				break;
			case 35: // is wand
				break;
			case 36: // uses
				I.setUsesRemaining(Util.s_int(old));
				break;
			case 37: // value
				I.setBaseValue(Util.s_int(old));
				break;
			case 38: // weight
				I.baseEnvStats().setWeight(Util.s_int(old));
				break;
			case 39: // is map
				break;
			case 40: // map areas
				if(I instanceof com.planet_ink.coffee_mud.interfaces.Map)
				{
					if(httpReq.isRequestParameter("MAPAREAS"))
					{
						old=";"+httpReq.getRequestParameter("MAPAREAS");
						for(int i=1;;i++)
							if(httpReq.isRequestParameter("MAPAREAS"+(new Integer(i).toString())))
								old+=";"+httpReq.getRequestParameter("MAPAREAS"+(new Integer(i).toString()));
							else
								break;
					}
					old=old+";";
					Sense.setReadable(I,false);
					I.setReadableText(old);
				}
				break;
			case 41: // is readable
				break;
			case 42: // is pill
				break;
			case 43: // is super pill
				break;
			case 44: // is potion
				break;
			case 45: // liquid types
				if((I instanceof Drink)&&(!(I instanceof Potion)))
					((Drink)I).setLiquidType(Util.s_int(old));
				break;
			case 46: // ammo types
				if(I instanceof Ammunition)
					((Ammunition)I).setAmmunitionType(old);
				else
				if((I instanceof Weapon)&&(!(I instanceof Wand)))
					((Weapon)I).setAmmunitionType(old);
				break;
			case 47: // ammo capacity
				if((I instanceof Weapon)&&(!(I instanceof Wand)))
				{
					((Weapon)I).setAmmoCapacity(Util.s_int(old));
					((Weapon)I).setAmmoRemaining(Util.s_int(old));
				}
				break;
			case 48: // readable spell
				if(I instanceof Wand)
					((Wand)I).setSpell(CMClass.findAbility(old));
				break;
			case 49: // is map
				break;
			case 50: // rideable type
				if(I instanceof Rideable)
					((Rideable)I).setRideBasis(Util.s_int(old));
				break;
			case 51: // mob capacity
				if(I instanceof Rideable)
					((Rideable)I).setRiderCapacity(Util.s_int(old));
				break;
			case 52: // has a lid
				if(I instanceof Container)
					((Container)I).setLidsNLocks(old.equals("on"),!old.equals("on"),((Container)I).hasALock(),((Container)I).hasALock());
				break;
			case 53: // has a lock
				if(I instanceof Container)
				{
					boolean hasALid=((Container)I).hasALid();
					((Container)I).setLidsNLocks(hasALid||old.equals("on"),!(hasALid||old.equals("on")),old.equals("on"),old.equals("on"));
				}
				break;
			case 54: // key code
				if((I instanceof Container)&&(((Container)I).hasALock()))
					((Container)I).setKeyName(old);
				break;
			case 55: // is wallpaper
				break;
			case 56: // nourishment
				if(I instanceof Food)
					((Food)I).setNourishment(Util.s_int(old));
				break;
			case 57: // container
				if(!RoomData.isAllNum(old))
					I.setContainer(null);
				else
				if(M==null)
					I.setContainer(RoomData.getItemFromCode(R,old));
				else
					I.setContainer(RoomData.getItemFromCode(M,old));
				break;
			case 58: // is light
				break;
			case 59:
				if(I instanceof Light)
					((Light)I).setDuration(Util.s_int(old));
				break;
			case 60:
				if(old.equals("on"))
					I.baseEnvStats().setSensesMask(I.baseEnvStats().sensesMask()|EnvStats.SENSE_UNLOCATABLE);
				else
				if((I.baseEnvStats().sensesMask()&EnvStats.SENSE_UNLOCATABLE)>0)
					I.baseEnvStats().setSensesMask(I.baseEnvStats().sensesMask()-EnvStats.SENSE_UNLOCATABLE);
				break;
			case 61: // is key
				break;
			case 62: // content types
				if((I instanceof Container)&&(httpReq.isRequestParameter("CONTENTTYPES")))
				{
					long content=Util.s_long(httpReq.getRequestParameter("CONTENTTYPES"));
					if(content>0)
					for(int i=1;;i++)
						if(httpReq.isRequestParameter("CONTENTTYPES"+(new Integer(i).toString())))
							content=content|Util.s_int(httpReq.getRequestParameter("CONTENTTYPES"+(new Integer(i).toString())));
						else
							break;
					((Container)I).setContainTypes(content);
				}
				break;
			case 63: // is instrument:
				break;
			case 64: // instrumenttype
				if(I instanceof MusicalInstrument)
					((MusicalInstrument)I).setInstrumentType(Util.s_int(old));
				break;
			case 65: // isammo
				break;
			case 66: // is mob type
				break;
			case 67: // is dust
				break;
			case 68: // is perfume
				break;
			case 69: // smells
				if(I instanceof Perfume)
					((Perfume)I).setSmellList(old);
				break;
			case 70:
			    I.setImage(old);
			    break;
			case 71: // is exit
			    break;
			case 72: // exit name
			    if(I instanceof Exit)
			        ((Exit)I).setExitParams(old,((Exit)I).closeWord(),((Exit)I).openWord(),((Exit)I).closedText());
			    break;
			case 73: // exit closed text
			    if(I instanceof Exit)
			        ((Exit)I).setExitParams(((Exit)I).doorName(),((Exit)I).closeWord(),((Exit)I).openWord(),old);
			    break;
			case 74: // numcoins
			    if(I instanceof Coins)
			        ((Coins)I).setNumberOfCoins(Util.s_long(old));
			    break;
			case 75: // currency
			    if(I instanceof Coins)
			        ((Coins)I).setCurrency(old);
			    break;
			case 76: // denomination
			    if(I instanceof Coins)
			        ((Coins)I).setDenomination(Util.s_double(old));
			    break;
			}
		}
		if(I.isGeneric())
		{
			String error=GrinderExits.dispositions(I,httpReq,parms);
			if(error.length()>0) return error;
			error=GrinderAreas.doAffectsNBehavs(I,httpReq,parms);
			if(error.length()>0) return error;
		}

		I.recoverEnvStats();
		I.text();
		if(itemCode.equals("NEW"))
		{
			if(M==null)
			{
				R.addItem(I);
				R.recoverRoomStats();
			}
			else
			{
				M.addInventory(I);
				M.recoverEnvStats();
				M.text();
				R.recoverRoomStats();
			}
		}
		else
		if(I!=oldI)
		{
			Environmental oldOwner=oldI.owner();
			if(M==null)
			{
				R.delItem(oldI);
				R.addItem(I);
				R.recoverRoomStats();
				for(int i=0;i<R.numItems();i++)
				{
					Item I2=R.fetchItem(i);
					if((I2.container()!=null)
					&&(I2.container()==oldI))
						if(I instanceof Container)
							I2.setContainer(I);
						else
							I2.setContainer(null);
				}
			}
			else
			{
				M.delInventory(oldI);
				M.addInventory(I);
				M.recoverEnvStats();
				M.text();
				R.recoverRoomStats();
				for(int i=0;i<M.inventorySize();i++)
				{
					Item I2=M.fetchInventory(i);
					if((I2.container()!=null)
					&&(I2.container()==oldI))
						if(I instanceof Container)
							I2.setContainer(I);
						else
							I2.setContainer(null);
				}
			}
			oldI.setOwner(oldOwner); // necesssary for destroythis to work.
			oldI.destroy();
		}
		if(M==null)
		{
			CMClass.DBEngine().DBUpdateItems(R);
			httpReq.addRequestParameters("ITEM",RoomData.getItemCode(R,I));
			R.startItemRejuv();
		}
		else
		{
			if((httpReq.isRequestParameter("BEINGWORN"))
			   &&((httpReq.getRequestParameter("BEINGWORN")).equals("on")))
			{
				if(I.amWearingAt(Item.INVENTORY))
					I.wearIfPossible(M);
			}
			else
				I.wearAt(Item.INVENTORY);
			CMClass.DBEngine().DBUpdateMOBs(R);
			httpReq.addRequestParameters("MOB",RoomData.getMOBCode(R,M));
			httpReq.addRequestParameters("ITEM",RoomData.getItemCode(M,I));
		}
		return "";
	}
}
