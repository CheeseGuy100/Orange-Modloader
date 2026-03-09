package com.modframework.ui.viewmodel

import com.modframework.ui.ModInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ModViewModel {
    private val _mods = MutableStateFlow<List<ModInfo>>(emptyList())
    val mods: StateFlow<List<ModInfo>> = _mods.asStateFlow()

    private val _selectedMod = MutableStateFlow<ModInfo?>(null)
    val selectedMod: StateFlow<ModInfo?> = _selectedMod.asStateFlow()

    init {
        _mods.value = sampleMods()
    }

    fun toggleMod(modId: String) {
        _mods.update { list ->
            list.map { mod ->
                if (mod.id == modId) mod.copy(isEnabled = !mod.isEnabled) else mod
            }
        }
        _selectedMod.update { selected ->
            if (selected?.id == modId) _mods.value.find { it.id == modId } else selected
        }
    }

    fun selectMod(mod: ModInfo?) { _selectedMod.value = mod }
    fun enableAll() { _mods.update { list -> list.map { it.copy(isEnabled = true) } } }
    fun disableAll() { _mods.update { list -> list.map { it.copy(isEnabled = false) } } }

    val enabledCount get() = _mods.value.count { it.isEnabled }
    val totalCount get() = _mods.value.size

    private fun sampleMods() = listOf(
        ModInfo("example_mod", "Example Mod", "1.0.0", "YourName",
            "A simple example mod showing Orange ModLoader features.", true, 2, 4),
        ModInfo("terrain_plus", "Terrain Plus", "2.3.1", "TerrainDev",
            "Overhauls world generation with new biomes and cave systems.", true, 12, 8),
        ModInfo("magic_spells", "Magic Spells", "0.9.4", "WizardCoder",
            "Adds a full spell system with 30+ spells.", false, 31, 15),
        ModInfo("better_ui", "Better UI", "1.1.0", "UIModder",
            "Improves the in-game HUD with cleaner health bars.", true, 0, 6),
        ModInfo("sound_pack", "Immersive Sounds", "1.0.2", "AudioDev",
            "Replaces default game sounds with high quality audio.", true, 0, 2)
    )
}