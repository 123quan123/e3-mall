package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemDescExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 商品管理Service
 * <p>Title: ItemServiceImpl</p>
 * <p>Description: </p>
 * @version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Resource
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${REDIS_CACHE_EXPIRE}")
	private Integer REDIS_CACHE_EXPIRE;
	@Value(("${REDIS_ITEM_SUF}"))
	private String REDIS_ITEM_SUF;
	@Value(("${REDIS_DESC_SUF}"))
	private String REDIS_DESC_SUF;

	@Override
	public TbItem getItemById(String itemId) {
		//添加緩存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + itemId + REDIS_ITEM_SUF);
			if (StringUtils.isNoneBlank(json)) {
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
		}
		//根据主键查询
		//TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andIdEqualTo(itemId);
		//执行查询
		List<TbItem> list = itemMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			try {
				jedisClient.set(REDIS_ITEM_PRE + itemId + REDIS_ITEM_SUF, JsonUtils.objectToJson(list.get(0)));
				jedisClient.expire(REDIS_ITEM_PRE + itemId + REDIS_ITEM_SUF, REDIS_CACHE_EXPIRE);

			} catch (Exception e) {
			}
			return list.get(0);
		}
		return null;
	}

	@Override
	public TbItemDesc selectItemDesc(Long id) {
		//添加緩存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + id + REDIS_DESC_SUF);
			if (StringUtils.isNoneBlank(json)) {
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return itemDesc;
			}
		} catch (Exception e) {
		}
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
		try {
			jedisClient.set(REDIS_ITEM_PRE + id + REDIS_DESC_SUF, JsonUtils.objectToJson(itemDesc));
			jedisClient.expire(REDIS_ITEM_PRE + id + REDIS_DESC_SUF, REDIS_CACHE_EXPIRE);

		} catch (Exception e) {
		}
		return itemDesc;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		//创建一个返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		//取分页结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		//取总记录数
		long total = pageInfo.getTotal();
		result.setTotal(total);
		return result;
	}

	@Override
	public E3Result addItem(final TbItem item, String desc) {

		item.setId(IDUtils.genItemId());
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		itemMapper.insert(item);
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		itemDesc.setItemDesc(desc);
		itemDescMapper.insert(itemDesc);

		jmsTemplate.send(topicDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				System.out.println("item.getId()" + item.getId() + "?>>>>>>>>>>>>>>>>>");

				return session.createTextMessage(item.getId() + "");

			}
		});

		return E3Result.ok();
	}

	@Override
	public E3Result updateItem(TbItem item, String desc) {
		// 1、根据商品id，更新商品表，条件更新
		TbItemExample itemExample = new TbItemExample();
		Criteria criteria = itemExample.createCriteria();
		criteria.andIdEqualTo(item.getId().toString());
		itemMapper.updateByExampleSelective(item, itemExample);
//		int i = itemMapper.updateByPrimaryKey(item);

		// 2、根据商品id，更新商品描述表，条件更新
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemDesc(desc);
		TbItemDescExample itemDescExample = new TbItemDescExample();
		TbItemDescExample.Criteria createCriteria =itemDescExample.createCriteria();
		createCriteria.andItemIdEqualTo(item.getId());
		itemDescMapper.updateByExampleSelective(itemDesc, itemDescExample);

		return E3Result.ok();
	}

//	@Override
//	public E3Result inOrOutStokeItem(String ids, int status) {
//		//判断ids不为空
//		if(StringUtils.isNoneBlank(ids)){
//			String[] split = ids.split(",");
//			//遍历成一个个的id进行修改下架
//			for ( String id : split ) {
//				//通过id查询到商品信息
//				TbItem item = itemMapper.selectByPrimaryKey(Long.valueOf(id));
//				//商品状态，1-正常，2-下架，3-删除
//				item.setStatus((byte) status);
//				//保存
//				itemMapper.updateByPrimaryKey(item);
//			}
//			return E3Result.ok();
//		}
//		return  null;
//	}

	@Override
	public E3Result dropoffItem(long[] itemId, TbItem item) {
		for (long l : itemId) {
			item = itemMapper.selectByPrimaryKey(l);
			item.setStatus((byte) 2);
			//创建时间不变
			item.setCreated(item.getCreated());
			//更新日期改变
			item.setUpdated(new Date());
			itemMapper.updateByPrimaryKeySelective(item);
		}

		return E3Result.ok();
	}

	/**
	 * 上架商品
	 */
	@Override
	public E3Result upperoffItem(long[] itemId, TbItem item) {
		for (long l : itemId) {
			item = itemMapper.selectByPrimaryKey(l);
			item.setStatus((byte) 1);
			item.setCreated(item.getCreated());
			item.setUpdated(new Date());
			itemMapper.updateByPrimaryKeySelective(item);
		}

		return E3Result.ok();
	}

	@Override
	public E3Result deleteBatch(String ids) {
		//判断ids不为空
		if (StringUtils.isNoneBlank(ids)) {
			//分割ids
			String[] split = ids.split(",");
			for (String id : split) {
				itemMapper.deleteByPrimaryKey(Long.valueOf(id));
				itemDescMapper.deleteByPrimaryKey(Long.valueOf(id));
			}
			return E3Result.ok();
		}
		return null;
	}
}
