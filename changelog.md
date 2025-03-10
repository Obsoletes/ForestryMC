## Forestry 1.0.8
- Remove bottler recipes from JEI
- Fix spectacles to show pollinated leaves and wild hives (#14)
- Fix double flower bee placement (#81)
- Creative hive frames now show in JEI and have epic rarity

## Forestry 1.0.7
- Sort products displayed in JEI by their chances
- Fix mating behaviour of unmated queens (ex. from Creative Menu)
- Fix Apiaries with frames producing more than Alvearies (#79)
- Fix blocks not dropping their inventories if blown up by Creeper (#52)
- Fix incorrect implementation of Haploid drone breeding option (#78)
- Fix creative frames having mixed up tooltips
- Fixed species mutations and biome-restricted bee mutations
- Fixed Fertile bee effect (#72)
- Fixed snowy hives not spawning in Grove biomes
- Fix fruits not spawning on tree leaves of non-default genome saplings (#82)
- Fix hives not spawning below y=0 (affects some custom/superflat worlds)

## Forestry 1.0.6
- Fixed wild beehives not being able to spawn in snowy areas (#56)
- Fix fruit squeezer recipes to use tags + correct mulch rates (#59)
- Fix Miner's bag not accepting raw ores (#58)
- Make all biomes in `#minecraft:is_nether` tag marked as having HELLISH climate (#65)
- The Phantasmal line (Ended, Spectral, Phantasmal) is now actually nocturnal and only works during the night time (unless given the NEVER_SLEEPS allele).
- Added Creative Frames for debugging Forestry mutations.
- Hive frames can now stack up to 64.
- Biomes in the end (tagged as `#minecraft:is_end`) will now use COLD / ARID climate.
- Fix Forester's Manual not working on dedicated server (#57, #60)
- Fix clientside console spam when opening menus using Database widgets (#74)
- Fix mutation date formatting to be more human-readable (#69)
- Add JEI support for bee/tree/butterfly mutations and products

## Forestry 1.0.5
- Fixed wild beehives not having particle effects
- Fix biogas engine lava tank accepting any fluid
- Fix still not working
- Added `forestry_fruits` tag to `forge:fruits` item tag + fruit tags for each fruit added by Forestry:
  - `forge:fruits/cherry` for Cherry
  - `forge:fruits/walnut` for Walnut
  - `forge:fruits/chestnut` for Chestnut
  - `forge:fruits/lemon` for Lemon, the same tag used by Fruits Delight
  - `forge:fruits/plum` for Plum
  - `forge:fruits/date` for Date
  - `forge:fruits/papaya` for Papaya
- Replaced ICheckPollinatable and IPollinatable with new IPollenType API.
  - A pollen type allows Forestry bees, butterflies, and Alveary Sieves to handle non-tree pollen types.
    To use, register a new IPollenType in `IForestryPlugin#registerPollen`.
- Fixed bug where "default" variants of Forestry leaves would never be used
- Fixed bug where fruit ripeness would sometimes not persist
- ACTUALLY make saplings and other items usable in the composter (#35)
- Increase energy storage of Bottler, Still, Fermenter by 10x
- Fixed bug where species would be able to mutate with themselves.
- Add haploid breeding option
- Fixed nether biome climates
- Savannah biomes are now WARM/ARID instead of HOT/ARID, making their climate distinct from the desert biomes.
- Fix pipette textures
- Fix bees not planting flowers (#53)
- Fix bronze crafting recipe only producing 1 ingot instead of 4 (#54)

## Forestry 1.0.4
- Added modify genome command. Can be used like `/forestry bee modify <chromosome> <allele> <both|dominant|recessive>` to
  modify the genome of the player's currently held genetic item (bees, saplings, butterflies)
- Fixed loading of Chinese (simplified) language file (#44)
- Fixed many translation keys in Chinese (simplified) language file (#45)
- Fixed Apiarist chest menu not displaying queens/princesses/drones in menu (#40)
- Unused honeycombs are no longer obtainable in survival, from village houses or trades (#42)
- Fixed fluid attributes and use honey/milk tags where possible
- Fixed liquid container menu desync (#29, #41)
- Fixed wild beehives dropping bees without a scoop
- Fixed legacy farms not planting from slots correctly (#43)

## Forestry 1.0.3
- Fixed saplings not dropping themselves (#36)
- Fixed villager professions not working (#38)
- Added new planks textures for all wood types
- Make saplings and other items usable in the composter (#35)
- Fix Forest and Tropical wild hive generation (#34, #37)
- Add TickHelper utility as public API
- Move factory methods from IBeeSpeciesType to IHiveManager for easier access.

## Forestry 1.0.2
- Fixed Naturalist backpack crash and bugs
- Fixed incorrect Apiarist backpack recipe
- Fixed Forestry wood/log naming to be consistent with Vanilla
- Fixed Analyzer not recognizing the vanilla Jungle Sapling
- Add missing recipes for Forestry wood/bark blocks
- Added Arborist backpack
- Add new textures for Apiary and Bee House
- Add new textures for Pine and Cherry planks (since they match Bee house and Apiary/Alveary planks)

## Forestry 1.0.1
- Add butterfly mating recipe
- Fix reobf crash with IFeatureSubtype
- Fix ore names and deepslate textures

## Forestry 1.0.0
- Initial release