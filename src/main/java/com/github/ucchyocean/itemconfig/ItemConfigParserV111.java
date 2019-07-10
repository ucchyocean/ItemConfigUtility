/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2017
 */
package com.github.ucchyocean.itemconfig;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

/**
 * アイテム設定のパーサー for Bukkit v1.11
 * @author ucchy
 */
public class ItemConfigParserV111 {

    /**
     * 指定されたアイテムがシュルカーボックスだったときに、メタ情報をセクションに保存する。
     * @param item
     * @param section
     */
    protected static void addShulkerBoxInfoToSection(ItemStack item, ConfigurationSection section) {

        if ( !isShulkerBox(item) ) return;

        if ( !item.hasItemMeta() || !(item.getItemMeta() instanceof BlockStateMeta) ) return;
        BlockStateMeta meta = (BlockStateMeta)item.getItemMeta();

        if ( !meta.hasBlockState() || !(meta.getBlockState() instanceof ShulkerBox) ) return;
        ShulkerBox box = (ShulkerBox)meta.getBlockState();

        // ボックスの中身をひとつひとつ保存する
        ConfigurationSection contentsSection = section.createSection("contents");
        Inventory inv = box.getInventory();
        for ( int index=0; index<inv.getSize(); index++ ) {
            ItemStack content = inv.getItem(index);
            if ( content == null || content.getType() == Material.AIR ) continue;
            ConfigurationSection contentSection = contentsSection.createSection("item" + index);
            ItemConfigParser.setItemToSection(contentSection, content);
        }
    }

    /**
     * シュルカーボックスのメタデータを含める必要がある場合に、メタ情報を復帰して含めておく。
     * @param item
     * @param section
     * @throws ItemConfigParseException
     * @return 変更後のItemStack
     */
    protected static ItemStack addShulkerBoxInfoToItem(ItemStack item, ConfigurationSection section)
            throws ItemConfigParseException {

        if ( !isShulkerBox(item) ) return item;

        if ( !section.contains("contents") ) return item;
        ConfigurationSection contentsSection = section.getConfigurationSection("contents");

        if ( !(item.getItemMeta() instanceof BlockStateMeta) ) return item;
        BlockStateMeta meta = (BlockStateMeta)item.getItemMeta();

        if ( !(meta.getBlockState() instanceof ShulkerBox) ) return item;
        ShulkerBox box = (ShulkerBox)meta.getBlockState();

        Inventory inv = box.getInventory();

        // 一旦内容をクリアする
        inv.clear();

        // ボックスの中身をひとつひとつ復帰する
        for ( String key : contentsSection.getKeys(false) ) {
            if ( !key.matches("item[0-9]+") ) continue;
            int index = Integer.parseInt(key.substring(4));
            ConfigurationSection contentSection = contentsSection.getConfigurationSection(key);
            try {
                ItemStack content = ItemConfigParser.getItemFromSection(contentSection);
                inv.setItem(index, content);
            } catch (ItemConfigParseException e) {
//                throw new ItemConfigParseException(
//                        "Shulker box content '" + key + "' is invalid.", e);
                continue;
            }
        }

        // 変更内容を適用する
        meta.setBlockState(box);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * シュルカーボックスの中に、指定された種類のアイテムが存在するかどうかをチェックする
     * @param materials アイテムの種類リスト
     * @param item シュルカーボックス
     * @return 指定された種類のアイテムが含まれているかどうか
     */
    public static boolean containsMaterialsInShulkerBox(List<Material> materials, ItemStack item) {

        if ( item == null || !isShulkerBox(item) ) return false;

        if ( !item.hasItemMeta() || !(item.getItemMeta() instanceof BlockStateMeta) ) return false;
        BlockStateMeta meta = (BlockStateMeta)item.getItemMeta();

        if ( !meta.hasBlockState() || !(meta.getBlockState() instanceof ShulkerBox) ) return false;
        ShulkerBox box = (ShulkerBox)meta.getBlockState();

        Inventory inv = box.getInventory();
        for ( ItemStack content : inv.getContents() ) {
            if ( content != null ) {
                for ( Material mat : materials ) {
                    if ( content.getType() == mat ) return true;
                }
            }
        }

        return false;
    }

    /**
     * シュルカーボックスの中に、指定された種類のアイテムが存在するかどうかをチェックする
     * @param materials アイテムの種類リスト(Material名のリスト)
     * @param item シュルカーボックス
     * @return 指定された種類のアイテムが含まれているかどうか
     */
    public static boolean containsMaterialStringInShulkerBox(List<String> materials, ItemStack item) {

        if ( item == null || !isShulkerBox(item) ) return false;

        if ( !item.hasItemMeta() || !(item.getItemMeta() instanceof BlockStateMeta) ) return false;
        BlockStateMeta meta = (BlockStateMeta)item.getItemMeta();

        if ( !meta.hasBlockState() || !(meta.getBlockState() instanceof ShulkerBox) ) return false;
        ShulkerBox box = (ShulkerBox)meta.getBlockState();

        Inventory inv = box.getInventory();
        for ( ItemStack content : inv.getContents() ) {
            if ( content != null ) {
                for ( String mat : materials ) {
                    if ( content.getType().toString().equals(mat) ) return true;
                }
            }
        }

        return false;
    }

    /**
     * 指定されたアイテムがシュルカーボックスかどうかを判定する
     * @param item アイテム
     * @return シュルカーボックスかどうか
     */
    public static boolean isShulkerBox(ItemStack item) {
        if ( item == null ) return false;
        Material type = item.getType();
        return type.name().endsWith("_SHULKER_BOX");
    }
}
