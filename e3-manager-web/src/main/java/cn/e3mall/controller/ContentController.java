package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.pojo.EasyUITreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内容管理Controller
 * <p>Title: ContentController</p>
 * <p>Description: </p>
 * @version 1.0
 */
@RestController
public class ContentController {
	
	@Autowired
	private ContentService contentService;

	@RequestMapping(value="/content/save", method=RequestMethod.POST)
	public E3Result addContent(TbContent item) {
		//调用服务把内容数据保存到数据库
		E3Result e3Result = contentService.addContent(item);
		return e3Result;
	}

	@RequestMapping(value="/rest/content/edit", method=RequestMethod.POST)
	public E3Result updateContent(TbContent item) {
		//调用服务把内容数据保存到数据库
		E3Result e3Result = contentService.updateContent(item);
		return e3Result;
	}

	@RequestMapping(value="/content/delete", method=RequestMethod.POST)
	public E3Result deleteContent(String[] ids, String categoryId) {
		E3Result e3Result = contentService.deleteContentBatch(ids);
		return e3Result;
	}
	@RequestMapping("/content/query/list")
	@ResponseBody
 	public EasyUIDataGridResult getContentList(TbContent item, long categoryId, int page, int rows) {
		EasyUIDataGridResult content=contentService.getContentList(categoryId,page,rows);
		return content;
	}
}
