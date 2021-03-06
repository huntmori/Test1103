package SteamCrawler.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;

import SteamCrawler.VO.AppVO;
import SteamCrawler.VO.TagVO;

public class Main 
{
	public static final int APP_VIEW = 1,
							AGE_CHECK=2,
							WARNNING=3;
	
	public ArrayList<TagVO> getAppTagList(Document doc, ArrayList<TagVO> allTags)
	{
		ArrayList<TagVO> result = new ArrayList<>();
		
		Elements tags= doc.getElementsByClass("glance_tags_label");
		System.out.println(tags.size());
		tags = tags.select("a[href][class]");
		
		for(Element e : tags)
		{
			if(e.className().equals("app_tag"))
			{
				String tagName = e.text();
				System.out.println(tagName);
			}
		}
		
		
		return result;
	}
	public ArrayList getAppInfo(String url) throws IOException
	{
		ArrayList	result	=	new ArrayList<>();
		
		Document document = (Document)Jsoup.connect(url).get();
		
		Elements metas = document.select("meta");
		
		System.out.println(metas.size());
		//metas = metas.select("meta");
		for(Element e:metas)
		{
			System.out.println(e.text());
			System.out.println(e.attr("itemprop")+"\t"+e.attr("content"));
		}
			
		return result;
	}
	
	public void getListInfo(String url) throws IOException
	{
		int page=1;
				
		Document doc = (Document) Jsoup.connect(url).get();
		//Document doc = (Document)Jsoup.connect("./Steam.html");
/*		String title = doc.title();
		
		
		ArrayList<String>	titleList	= new ArrayList<String>();
		ArrayList<String>	appURLList	= new ArrayList<String>();
		ArrayList<String>	appIdList	= new ArrayList<String>();*/
		
		ArrayList<AppVO>	infoList	=	new ArrayList<AppVO>();
		
		PrintWriter output = new PrintWriter(new File("app_list.txt"));
		output.println("title\tappid\turl");
		//set max page numb
		
			
			/*for(Element e : ele )
			{
				String temp = e.text().replaceAll(" ","_");
				//System.out.println(temp);
				titleList.add(temp);
			}
			
			
			for(Element e:linkList)
			{
				appURLList.add(e.attr("href"));
			}
			
			
			for(Element e : links)
			{
				String href = e.attr("data-ds-appid");
				appIdList.add(href);
			}*/
		
		
		
		
		/*for(int i=0; i<titleList.size(); i++)
		{
			String appid = (String)appIdList.get(i);
			int test = appid.indexOf(",");
			
			String strurl = (String)appURLList.get(i);
			int test2 = strurl.indexOf("app");
			
			if(test==-1 && test2!=-1)
			{
				System.out.println(titleList.get(i)+"\t"+appIdList.get(i)+"\t"+appURLList.get(i));
				output.println(titleList.get(i)+"\t"+appIdList.get(i)+"\t"+appURLList.get(i));
			}				
		}		*/
		
	}
	
	public int	checkPageType(String url)
	{
		int testAgeCheck = url.indexOf("agecheck");
		/*System.out.println(testAgeCheck);
		System.out.println(url.length());
		System.out.println(url.length()-testAgeCheck);*/
		
		if(testAgeCheck==-1)
		{//상품 view임
			return APP_VIEW;
		}
		
		else if(url.length()-testAgeCheck==8)
		{// warning page 8 
			return WARNNING;
		}
		else
		{//	AgeCheckPage
			return AGE_CHECK;
		}
	}
	public HtmlPage ConnectByWebClient(String url)
	{
		//웹 클라이언트 생성 및 설정
		@SuppressWarnings("resource")
		WebClient webClient = new WebClient(BrowserVersion.CHROME); 
		webClient.addRequestHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4, value"); 
		webClient.addRequestHeader("Accept-Charset", "windows-949,utf-8;q=0.7,*;q=0.3"); 
		webClient.getCookieManager().setCookiesEnabled(true); 
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(true);
		//java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		 
		//URL 인코딩
		try {
			URLEncoder.encode(url,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		
		// page를 받아옴
		HtmlPage page = null;
		try {
			page = webClient.getPage(url);
		}
		catch (FailingHttpStatusCodeException | IOException e1){
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		return page;
	}
	
	public Document ReconnectWarnningPage(String url)
	{
		HtmlPage page = ConnectByWebClient(url);
		HtmlAnchor button = page.getAnchorByText("View Page");
		
		HtmlPage result = null;
		try {
			result = button.click();
			WebResponse data = result.getWebResponse();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println(result.toString());
		
		Document doc = new Document("");//Jsoup.parse(result.asXml());
		doc.html(result.asXml());
		 
		System.out.println(doc.toString());
		return doc;
	}
	
	public Document ReconnectAgecheck(String url)
	{
		// 웹 클라이언트 초기화
		HtmlPage page = ConnectByWebClient(url);
		
		// 페이지 상의 모든 폼을 받아온다.
		List<HtmlForm> formlist = page.getForms();
		
		//연령체크폼 초기화
		HtmlForm ageCheckForm = null;
		
		for(HtmlForm frm : formlist){
			//페이지 상의 폼 중에서 id가 agecheck_form인 요소를 할당
			if(frm.getId().equals("agecheck_form"))
				ageCheckForm = frm;
		}
		
		// POST방식 전송 데이터 할당
		ageCheckForm.getSelectByName("ageDay").setDefaultValue("31");
		ageCheckForm.getSelectByName("ageMonth").setDefaultValue("May");
		ageCheckForm.getSelectByName("ageYear").setDefaultValue("1989");
		
		System.out.println(ageCheckForm.getSelectByName("ageDay").getDefaultValue());
		System.out.println(ageCheckForm.getSelectByName("ageMonth").getDefaultValue());
		System.out.println(ageCheckForm.getSelectByName("ageYear").getDefaultValue());

		//Submit 버튼을 받아옴
		HtmlAnchor button = page.getAnchorByText("Enter");
		HtmlPage result = null;
		
		 
		try
		{
			// 버튼 클릭을 하여 다음 페이지를 받아온다
			System.out.println(button.getOnClickAttribute());
			result = button.click();
			//System.out.println(result.getWebResponse());
			
			
			// 받아온 응답을 파싱하여 Document 생성			 
			Document doc = Jsoup.parse(result.toString());
			
			 
			//System.out.println(doc.title());
			return doc;
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		return null;
	}
	
	public static Document ConnectionJsoup(String url) throws IOException
	{
		//birthtime=-63190799; path=/
		//lastagecheckage=1-January-1968;
		//expires=Sat, 27-Oct-2018 14:59:26 GMT; Max-Age=31536000; path=/
		Document result	=	(Document)Jsoup.connect(url).get();
		
		System.out.println(result.location());
		
		return result;
	}
		
	public Main() 
	{
		//List URL
		String url = "http://store.steampowered.com/search/?category1=998&page=";
		String soma= ("http://store.steampowered.com/app/282140");
		String pubg="http://store.steampowered.com/app/578080/agecheck";
		String dyinglight="http://store.steampowered.com/agecheck/app/239140/";
		String beyoneta="http://store.steampowered.com/app/460790/agecheck";
		String rocketLeague = "http://store.steampowered.com/app/252950/Rocket_League/";
		String mordorGOTY = "http://store.steampowered.com/app/318550/Middleearth_Shadow_of_Mordor__GOTY_Edition_Upgrade/";
		String	mordor="http://store.steampowered.com/app/241930/Middleearth_Shadow_of_Mordor/";
		String temp = "http://steamcommunity.com/app/379720/positivereviews/?browsefilter=toprated&snr=1_5_reviews_&filterLanguage=koreana&p=1";
		
		//Document doc = ConnectionJsoup(mordorGOTY);
		String osiris="http://store.steampowered.com/app/402710/Osiris_New_Dawn/";
		String deusEx = "http://store.steampowered.com/app/337000/Deus_Ex_Mankind_Divided/?snr=1_5_9__412";
		//ReconnectAgecheck(deusEx);
		
		Document doc = ReconnectWarnningPage("http://store.steampowered.com/app/578080/PLAYERUNKNOWNS_BATTLEGROUNDS/");
		new AppInfoCrawler(doc).ProccessCrawl();
		//ReconnectWarnningPage(pubg);
		
		/*
		AppReviewCrawler rv = new AppReviewCrawler();
		rv.appid = "402710";
		System.out.println("Positive");
		rv.test(rv.getPositiveURL());
		System.out.println("============================");
		System.out.println("Negative");
		rv.test(rv.getNegativeURL());
		System.out.println("============================");
		getAppTagList(doc, null);
		String assassin = "http://store.steampowered.com/app/582160/Assassins_Creed_Origins/";
		AppInfoCrawler app=new AppInfoCrawler(osiris);
		app.ProccessCrawl();*/
		/*Map<String, String> ageCheckData = new HashMap<String, String>();
		
		ageCheckData.put("ageDay", "23");
		ageCheckData.put("ageMonth", "May");
		ageCheckData.put("ageYear", "1989");
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF); 
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

		
		
		
		String listurl = "http://store.steampowered.com/search/?category1=998&page=";
		AppListCrawler appList = new AppListCrawler(listurl);
		appList.ProccessCrawl(new PrintStream(new File("app_list.txt")));
		
		//System.out.println();
		
		//System.out.println(deus.getElementsByClass("app_tag").get(0).text());*/
	}
	public static void main(String[] args)
	{
		new Main();
	}
}