package com.planet_ink.coffee_mud.Libraries.interfaces;
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
import com.planet_ink.coffee_mud.Libraries.Quests;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
import java.util.*;
/* 
   Copyright 2000-2006 Bo Zimmerman

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
public interface QuestManager extends CMLibrary
{
    public Quest objectInUse(Environmental E);
    public int numQuests();
    public Quest fetchQuest(int i);
    public Quest fetchQuest(String qname);
    public void addQuest(Quest Q);
    public void shutdown();
    public void delQuest(Quest Q);
    public void save();
    public Vector parseQuestSteps(Vector script, int startLine, boolean rawLineInput);
    public Vector parseQuestCommandLines(Vector script, String cmdOnly, int startLine);
    public int getHolidayIndex(String named);
    public String getHolidayName(int index);
    public String listHolidays(Area A, String otherParms);
    public String deleteHoliday(Area A, int holidayNumber);
    public void modifyHoliday(MOB mob, int holidayNumber);
    public String createHoliday(Area A, String named);
    public Object getHolidayFile();
    public Vector getEncodedHolidayData(String dataFromStepsFile);
}
