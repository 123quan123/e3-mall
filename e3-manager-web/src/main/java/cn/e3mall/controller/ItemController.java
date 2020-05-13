package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItemDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Controller
 * <p>Title: ItemController</p>
 * <p>Description: </p>
 *
 * @version 1.0
 */
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 测试
     * @param itemId
     * @return
     */
    @RequestMapping("/item/{itemId}")
    public TbItem getItemById(@PathVariable String itemId) {
        TbItem tbItem = itemService.getItemById(itemId);
        return tbItem;
    }

    /**
     * 查询商品
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/item/list")
    public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
        EasyUIDataGridResult gridResult = itemService.getItemList(page, rows);
        return gridResult;
    }

    /**
     * 商品添加
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping(value = "/item/save", method = RequestMethod.POST)
    public E3Result addItem(TbItem item, String desc) {
        E3Result result = itemService.addItem(item, desc);
        return result;
    }

    /**
     * 批量删除功能实现
     * @param ids
     * @return
     */
    @RequestMapping("/rest/item/delete")
    public E3Result deleteItem(String ids, TbItem item) {
        E3Result result = itemService.deleteBatch(ids);
        return result;
    }

    /**
     * 异步重新加载回显描述
     * @param id
     * @return
     */
    @RequestMapping("/rest/item/query/item/desc/{id}")
    public TbItemDesc selectTbItemDesc(@PathVariable long id){
        TbItemDesc itemDesc= itemService.selectItemDesc(id);
        return itemDesc;
    }

    /**
     * 异步重新加载商品信息
     * @param id
     * @return
     */
    @RequestMapping("/rest/item/param/item/query/{id}")
    public TbItem queryById(@PathVariable String id) {
        TbItem item = itemService.getItemById(id);
        return item;
    }



    // 上架商品
    @RequestMapping(value = "/rest/item/reshelf", method = RequestMethod.POST)
    @ResponseBody
    private E3Result upperoffItem(@RequestParam("ids") long []itemId, TbItem item) {
        E3Result result = itemService.upperoffItem(itemId, item);
        return result;
    }

    // 下架商品
    @RequestMapping(value = "/rest/item/instock", method = RequestMethod.POST)
    @ResponseBody
    private E3Result dropoffItem(@RequestParam("ids") long []itemId, TbItem item) {
        E3Result result = itemService.dropoffItem(itemId, item);
        return result;
    }

    /**
     * 更新商品
     * @return
     */
    @RequestMapping("/rest/item/update")
    public E3Result updateItem(TbItem item, String desc) {
        System.out.println(item);
        E3Result result = itemService.updateItem(item, desc);
        return result;
    }


}
