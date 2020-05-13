package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;

/**
 * 内容管理Service
 * <p>Title: ContentServiceImpl</p>
 * <p>Description: </p>
 * @version 1.0
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;

	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	
	@Override
	public E3Result addContent(TbContent content) {
		//将内容数据插入到内容表
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//插入到数据库
		contentMapper.insert(content);
		jedisClient.hdel(CONTENT_LIST,content.getCategoryId() + "");
		return E3Result.ok();
	}

	@Override
	public E3Result deleteContentBatch(String[] ids) {
		//判断ids不为空
		System.out.println(ids);
		TbContent content = null;
		for (String id : ids) {
			contentMapper.deleteByPrimaryKey(Long.valueOf(id));
//			if (content == null) {
//				content = contentMapper.selectByPrimaryKey(Long.valueOf(id));
//			}
		}

//		jedisClient.hdel(CONTENT_LIST,content.getCategoryId() + "");
		return E3Result.ok();
	}

	/**
	 * 根据内容分类id查询内容列表
	 * <p>Title: getContentListByCid</p>
	 * <p>Description: </p>
	 * @param cid
	 * @return
	 * @see ContentService#getContentListByCid(long)
	 */
	@Override
	public List<TbContent> getContentListByCid(long cid) {
		try {
			//查询缓存，若有直接响应，若异常trycache
			String list = jedisClient.hget(CONTENT_LIST, cid + "");
			if (StringUtils.isNoneBlank(list)) {
				List<TbContent> contentList = JsonUtils.jsonToList(list, TbContent.class);
				return contentList;
			}
		} catch (Exception e) {
		}
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andCategoryIdEqualTo(cid);
		//执行查询
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);

		try {
			//没有，查询数据库，添加到缓存
			jedisClient.hset(CONTENT_LIST, cid+"", JsonUtils.objectToJson(list));
		} catch (Exception e){
		}


		return list;
	}

	@Override
	public E3Result updateContent(TbContent content) {
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(content.getId());
		contentMapper.updateByExampleSelective(content, example);
		jedisClient.hdel(CONTENT_LIST,content.getCategoryId() + "");
		return E3Result.ok();
	}

	@Override
	public EasyUIDataGridResult getContentList(long categoryId, int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> contentList = contentMapper.selectByExample(example);
		//创建一个返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(contentList);
		//取分页结果
		PageInfo<TbContent> pageInfo = new PageInfo<>(contentList);
		//取总记录数
		long total = pageInfo.getTotal();
		result.setTotal(total);
		return result;
	}
}
