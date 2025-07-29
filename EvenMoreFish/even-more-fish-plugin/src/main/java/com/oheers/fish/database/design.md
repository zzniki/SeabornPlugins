TODO Remove once done


This design doc is a helpful tool for us to track the changes needed to be made in the database.

## Fish Journal

### FishLog - Should track just the fish catches by players and any additional info for that catch.
* user id 
* fish name
* rarity
* catch time
* length

* competition config id?

### Competition Report - Should track competitions after they are completed

* competition config id
* winner fish
* winner uuid
* List<String> contestants
* winner score


* start time, end time

### Fish Stats - a per fish stats report

* fishName
* fishRarity
* first catch time
* shortest length
* shortest fisher
* longest length
* longest fisher
* total quantity caught so far

### User Fish Stats

* user id
* fish name
* fish rarity
* first catch time
* shortest length
* longest length
* total quantity

### User Report

* user id
* uuid
* firstFish ever caught
* largest fish
* total amount of fish caught
* competitions won
* competitions joined
* total fish length ???
* largest length
* quantity of fish sold
* total money earned


## To Do

1. [*] Finish base models
2. [*] Set up base migrations (Create Tables first)
3. [ ] Create admin commands to easily view this info. Can use the "admin database". Also add an option to "regenerate" the stats from the log. Not high priority so add this as an issue.
4. [ ] Update the gui impl to reflect these changes.
5. [ ] Write migrations for 7.1 -> 8.1
6. [ ] Test with MySQL & SQLite
