# Orange-Modloader
"Built with the help of Claude (Anthropic)"
# ModFramework

A universal Lua-based modding engine for Java games. Add a full modding system to your game in minutes.

## Features
- 🔌 **Mod Loader** - Auto-discovers and loads mod folders
- 🎣 **Event Hooks** - Mods listen to game events via Lua functions
- 📦 **Content Registry** - Mods register new items, entities, blocks, etc.
- 🔒 **Sandboxed Lua** - Each mod runs in its own safe Lua environment
- ❌ **Cancellable Events** - Mods can cancel player actions and game logic
- 🏆 **Priority System** - Control the order listeners fire

---

## Quick Start (Game Developer)

### 1. Add the dependency
```xml
<dependency>
    <groupId>com.modframework</groupId>
    <artifactId>modframework</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Initialize the framework
```java
// During game startup
File modsDir = new File("mods");
ModFramework framework = ModFramework.init(modsDir);
```

### 3. Fire events from your game
```java
// When a player joins
EventContext ctx = new EventContext()
    .set("playerName", player.getName())
    .set("x", player.getX())
    .set("y", player.getY());

ModFramework.getInstance().fireEvent("player.join", ctx);

// When player takes damage - mods can cancel it
EventContext dmgCtx = new EventContext()
    .set("damage", 25.0)
    .set("hasShield", player.hasShield());

ModFramework.getInstance().fireEvent("player.damage", dmgCtx);

if (dmgCtx.isCancelled()) {
    // A mod cancelled the damage - don't apply it
}
```

### 4. Query mod-registered content
```java
// Get all items registered by mods
Map<String, Map<String, Object>> modItems = 
    ModFramework.getInstance().getContentRegistry().getAll("items");

for (Map.Entry<String, Map<String, Object>> entry : modItems.entrySet()) {
    String itemId = entry.getKey();           // e.g. "example_mod:fire_sword"
    Map<String, Object> data = entry.getValue();
    String name = (String) data.get("name"); // e.g. "Fire Sword"
    double damage = (double) data.get("damage"); // e.g. 25.0
}
```

### 5. Shutdown cleanly
```java
// During game shutdown
ModFramework.getInstance().shutdown();
```

---

## Mod Structure

Each mod is a folder inside the `mods/` directory:

```
mods/
  my_cool_mod/
    mod.json     ← metadata (required)
    main.lua     ← entry point (required)
    assets/      ← optional assets folder
```

### mod.json
```json
{
  "id": "my_cool_mod",
  "name": "My Cool Mod",
  "version": "1.0.0",
  "author": "YourName",
  "description": "Adds cool stuff to the game"
}
```

### main.lua - The Modding API

```lua
-- Register new content
mod.register("items", "ice_staff", {
    name = "Ice Staff",
    damage = 20,
    element = "ice",
    texture = "ice_staff.png"
})

-- Listen to game events
mod.on("player.join", function(ctx)
    mod.log("Player joined: " .. ctx.playerName)
end)

-- Listen with priority (higher runs first)
mod.on("player.damage", function(ctx)
    if ctx.hasShield then
        ctx.cancelled = true  -- cancel the damage!
    end
end, 10)

-- Log messages
mod.log("My mod loaded!")

-- Access mod info
mod.log("I am: " .. mod.id .. " v" .. mod.version)
```

---

## API Reference

### Content Registration
| Function | Description |
|----------|-------------|
| `mod.register(category, id, data)` | Register new content. Category can be anything: "items", "entities", "blocks", etc. |

### Events
| Function | Description |
|----------|-------------|
| `mod.on(event, handler)` | Listen to an event |
| `mod.on(event, handler, priority)` | Listen with priority (default 0, higher = runs first) |

### Cancelling Events
Set `ctx.cancelled = true` in a handler to cancel the event (if the game checks for it).

### Logging
| Function | Description |
|----------|-------------|
| `mod.log(message)` | Print a message to the game log |

### Mod Info
| Field | Description |
|-------|-------------|
| `mod.id` | This mod's ID |
| `mod.version` | This mod's version |
| `mod.author` | This mod's author |
| `mod.path` | Absolute path to this mod's folder |

---

## Building

```bash
mvn package
```

This produces a fat JAR in `target/` with LuaJ included.
AND ITS OPEN SOURCE
---

## License
MIT - free to use in open source and commercial games.
