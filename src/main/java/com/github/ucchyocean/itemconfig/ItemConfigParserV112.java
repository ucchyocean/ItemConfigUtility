/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2017
 */
package com.github.ucchyocean.itemconfig;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

/**
 * アイテム設定のパーサー for Bukkit v1.12
 * @author ucchy
 */
public class ItemConfigParserV112 {

    /**
     * 指定されたアイテムがナレッジブックだったときに、メタ情報をセクションに保存する。
     * @param item
     * @param section
     */
    protected static void addKnowledgeBookInfoToSection(ItemStack item, ConfigurationSection section) {

        if ( item.getType() != Material.KNOWLEDGE_BOOK ) return;

        if ( !item.hasItemMeta() || !(item.getItemMeta() instanceof KnowledgeBookMeta) ) return;
        KnowledgeBookMeta meta = (KnowledgeBookMeta)item.getItemMeta();

        if ( !meta.hasRecipes() ) return;

        ConfigurationSection recipesSection = section.createSection("recipes");
        int index = 0;

        for ( NamespacedKey key  : meta.getRecipes() ) {
            recipesSection.set("recipe" + index, key.getKey());
            index++;
        }
    }

    /**
     * ナレッジブックのメタデータを含める必要がある場合に、メタ情報を復帰して含めておく。
     * @param item
     * @param section
     * @return
     * @throws ItemConfigParseException
     */
    protected static ItemStack addKnowledgeBookInfoToItem(ItemStack item, ConfigurationSection section)
            throws ItemConfigParseException {

        if ( item.getType() != Material.KNOWLEDGE_BOOK ) return item;

        if ( !section.contains("recipes") ) return item;
        ConfigurationSection recipesSection = section.getConfigurationSection("recipes");

        if ( !(item.getItemMeta() instanceof KnowledgeBookMeta) ) return item;
        KnowledgeBookMeta meta = (KnowledgeBookMeta)item.getItemMeta();


        for ( String key : recipesSection.getKeys(false) ) {
            String recipe = recipesSection.getString(key);
            meta.addRecipe(NamespacedKey.minecraft(recipe));
        }

        item.setItemMeta(meta);

        return item;
    }

}
