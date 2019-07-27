package com.shadow.gmall.list;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.PmsProductInfo;
import com.shadow.gmall.beans.PmsSearchSkuInfo;
import com.shadow.gmall.beans.PmsSkuInfo;
import com.shadow.gmall.service.PmsProductInfoService;
import com.shadow.gmall.service.PmsSkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {
	@Reference
	private PmsSkuInfoService pmsSkuInfoService;

	@Autowired
	private JestClient jestClient;

	@Test
	public void contextLoads() throws IOException {


		Search search=new Search.Builder("{\n" +
				"  \"query\": {\n" +
				"    \"match_all\": {}\n" +
				"  }\n" +
				"}").addIndex("movie_chn").addType("movie").build();
		//查询数据库中所有的pmsSkuInfo
		List<PmsSkuInfo> pmsProductInfoList=this.pmsSkuInfoService.selectAll();
		for (PmsSkuInfo pmsSkuInfo : pmsProductInfoList) {
			PmsSearchSkuInfo pmsSearchSkuInfo=new PmsSearchSkuInfo();
			//将pmsSkuInfo的信息输入pmsSearchSkuInfo
			BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
			//解决pmsSearchSkuInfoid映射不上的问题
			System.out.println(pmsSkuInfo.getId());
			pmsSearchSkuInfo.setId(new Long(pmsSkuInfo.getId()));
			//将pmsSearchSkuInfo输入elasticsearch
			Index index=new Index.Builder(pmsSearchSkuInfo).index("gmall").type("PmsSearchSkuInfo").id(pmsSkuInfo.getId()).build();
			JestResult execute =jestClient.execute(index);
		}

	}

}
