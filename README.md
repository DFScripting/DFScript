[![Build](https://github.com/DFScripting/DFScript/actions/workflows/build.yml/badge.svg)
](https://github.com/DFScripting/DFScript/actions/workflows/build.yml)
# DFScript
DFScript is a mod created for DiamondFire to allow scripting on the client. This mod is a seperation and improvement from the shutdown mod [CodeUtilities](https://github.com/CodeUtilities/CodeUtilities).

- You can download DFScript from its [Github page](https://github.com/DFScripting/DFScript/releases/latest), or its [Modrinth page](https://modrinth.com/mod/dfscript).
- You can report issues on [the issues tab](https://github.com/DFScripting/DFScript/issues)

- You can join our [discord here](https://discord.gg/gtfFwWEapx)

## About
DFScript adds the ability to add new features through the added 
'scripting' ability. Scripting is constructing event based code (similar to DiamondFire) inside a client side menu, with the events and actions being simple client-based activities. 

For example,  a script could play a sound when the player receives a message with their name in it, or automatically add the @ character in front of sent messages.
___

To access the scripting menu, run `/scripts`,  and a menu will appear showing all of the scripts you have installed. Here you can toggle, delete, edit and upload scripts.
*(A simple menu which can disable scripts can be accessed through the button in the top left.)* 

To install a script you can click 'Add' and browse through scripts that others have created, or select 'New Script' if you would like to create one yourself.

## Creating Scripts

To create a new script, run `/script`, select 'Add', and 'New Script'. After naming your script, it will be created and displayed in the main menu. From here, you can edit, upload, delete, and toggle your script. 

Here is what each of these actions do:
- ![Edit icon](https://i.imgur.com/T01dmur.png) Edit: From this menu, you can add options and code to your script. 
- ![Upload icon]( https://i.imgur.com/0FuRXJ1.png) Upload: Uploads the script so that others with the mod can install and use it. (They can not modify the code)
- ![Delete icon](https://i.imgur.com/Ya9xyWH.png) Delete: Deletes your script permanently.
- ![enter image description here](https://i.imgur.com/Av7lZmu.png)**/**![enter image description here](https://i.imgur.com/I8PXtvr.png) Toggle: Toggles your script on and off. When a script is off, it does not run at all. (Red means script is currently on, green means script is off)

To edit your script, select the edit icon and a new menu will be opened. From here, you can add code, change your scripts description and add config options.

- ![Settings Icon](https://i.imgur.com/fy9SfQP.png) From here you can write a short description about your script to tell others who may use it any important information. 
You can also add config options which users can change without accessing the code. The values of these options can be used in your code. (More info [later](https://github.com/not-first/DFScript-updated-readme-/edit/1.19.2/README.md#changing-parameters))



## Interactions
**Left click**
- Use on buttons to click them
- Use to add code to your script, and to add parameters to code blocks
- Use in your script menu to edit parameters of a code block
- Use to select text box

**Right Click**
- Use on code blocks in your script menu to bring up a small menu where you can:
  - Delete the code block
  - Insert code before the code block
  - Insert new code after the code block
- Can also be used on buttons to click them

**Escape**
- Use in any menu to go back to the previous menu

 **Scroll**
- Moves a menu up and down

## Editing Scripts
*Note that it is only possible to edit code in a script you have made yourself. Any downloaded script will still be able to be deleted, toggled, and have its options changed, but the code will be inaccessible.*

Select the 'Add' button to add code to your script. From there you can select the category, and a specific code block. There is a short description about each block if you hover over them to help you understand what they do.

All code is set in one menu. Just like in DiamondFire, all code must start with an event. When that event is triggered, the code inside it will run from top to bottom.

### Changing Parameters
Most code blocks need parameters inside them so they can execute properly. You can view what parameters a code blocks needs by hovering over its icon in your script menu. An asterisk after a parameter means that a value for it is not required and the code block can run without it.

To add values for these parameters, left click on the code block you would like to edit and a menu will be brought up. Any parameters already in the code block will show. To add values, click the 'Add' button, and a new menu with a text input box will appear.
___
This input screen has two important sections. 
1. The text box. Here is where you input a number, string, name of a variable or any other type of value required. Make sure you have finished the value you would like to add to your code block before selecting a type.
2. The value type. Here is where you select what type of value you would like to add to your code block. For example, if I wanted to add a variable called 'score', I would type `score` in the text box, then click on the variable icon (magma cream). A variable named 'score' will be added to the code block.
Most value types and their icons such as a text, a number and a variable you may recognise from DiamondFire, but here are some ones you may not be familiar with:

	- Client Value: *(Requires no text input.)* Selecting this value will bring up a menu containing values related to the client, very similar to DiamondFire's game values. 
	Some of these can be used in any scenario, such as the number of the players selected hotbar slot, or the players main hand item. 
	Others can only used if the code is under a certain event, such as the last received message *(pairs with the 'OnRecieveChat' event)* or the last received sound *(pairs with the 'OnRecieveSound' event)*.
	- Config Value: *(Requires no text input.)* Selecting this value will bring up a menu of all the config options you have added to your script. These will act as a variable and return a value of the option. 
For example, if I had created Option 1 as a string, and the user had set it to `complete`, the config value for options 1 would return 'complete' as a string.
These can be nearly any value type, so pay attention to make sure the code block you are using supports the value type.

*Currently, changing the order and contents of added parameters is not supported, so you will have to delete and redo if you make a mistake.*
## Disabling Scripts - !!
Sometimes when you are creating a script, you may accidentally make your client complete actions which are against DiamondFire rules, such as making your client spam, or other similar actions. If this is the case, immediately leave the server. In the top left if your multiplayer screen, there will be a button button (![Scripts icon](  https://i.imgur.com/Q2phpZz.png)) which will open up a version of the scripts menu where scripts can be disabled. When you rejoin the server the script will now be disabled. **You can edit code inside a disabled script, so make sure you have fixed the issue before re-enabling the script.**
If the issue came from a script you did not make, report the script in the [discord](https://discord.gg/gtfFwWEapx) so it can be removed.
## Downloading Scripts
To download a script that someone else has made, open the main scripts menu, select add, and there will be a list of scripts others have made as well as a search bar. Scripts with a star next to their name mean that they are verified and safe to use.

**!! Unverified scripts may potentially be dangerous, not towards your computer, but they may make your client spam or do other activities not appreciated on DiamondFire. You can ask in the discord if you are unsure about a certain script. !!**

Selecting a script from here will bring up a menu showing information about the script such as the authors name, script ID, and if it has been approved or not.
Clicking the install button from here will add the script to your client. A message will be shown in chat verifying the script has been loaded. This downloaded script will appear in the normal scripts menu, just without the ability to upload it or edit its code.

## Commands and Files

All DFScript commands begin with `/scripts`. 
- `/scripts` Opens the main script menu.
- `/scripts recursion (number)` Changes the number of events that can trigger in a single tick.
- `/scripts reload` Reloads all of the scripts on your machine. Use this is you are manually installing are changing scripts through files.
- `/scripts vars (script name)` Lists the values of all the variables in the selected script. Similar to DiamondFire's /p vars command.

To access script files go to `%appdata%\.minecraft\DFScript` on your machine. Here there will be a 'Scripts' folder, which contains all the scripts you have installed in the form of a json file. If a script manages text files, they will be in a sub folder called `scriptname.json-files`. 

You can easily back up scripts here by copy and pasting the json file into another location. Pasting and deleting json files from this folder will do the same in-game, so be careful about what you change.
___

<p>
<img src="https://img.shields.io/github/downloads/DFScripting/DFScript/total?color=blue"/>
<img src="https://img.shields.io/github/languages/code-size/DFScripting/DFScript"/>
  <img src="https://img.shields.io/github/contributors/DFScripting/DFScript" />
  <img src="https://img.shields.io/github/release-date/DFScripting/DFScript" /> 
 </p>
