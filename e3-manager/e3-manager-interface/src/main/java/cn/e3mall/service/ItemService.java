package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

public interface ItemService {

	TbItem getItemById(String itemId);

	EasyUIDataGridResult getItemList(int page, int rows);

	E3Result addItem(TbItem item, String desc);

	TbItemDesc selectItemDesc(Long id);

	E3Result updateItem(TbItem item, String desc);

	/**
	 * 商品下架
	 */
	E3Result dropoffItem(long[] itemId, TbItem item);

	/**
	 * 商品上架
	 */
	E3Result upperoffItem(long[] itemId, TbItem item);

	E3Result deleteBatch(String ids);
}
