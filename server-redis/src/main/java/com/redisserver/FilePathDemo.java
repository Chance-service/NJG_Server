package com.redisserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FilePathDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 鐏忎浇顥婇惄顔肩秿,闂囷拷鐟曚椒鎱ㄩ弨瑙勬瀮娴犺埖鐗稿蹇曟畱鐠侯垰绶�
		File srcFolder = new File("../../");
 
		// 闁帒缍婇崝鐔诲厴鐎圭偟骞�
		try {
			getAllJavaFilePaths(srcFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void getAllJavaFilePaths(File srcFolder) throws IOException {
		 
		// 閼惧嘲褰囩拠銉ф窗瑜版洑绗呴幍锟介張澶屾畱閺傚洣娆㈤幋鏍拷鍛瀮娴犺泛銇欓惃鍑le閺佹壆绮�
		File[] fileArray = srcFolder.listFiles();
 
		// 闁秴宸荤拠顧宨le閺佹壆绮嶉敍灞界繁閸掔増鐦℃稉锟芥稉鐙le鐎电钖�
		for (File file : fileArray) {
 
			// 缂佈呯敾閸掋倖鏌囬弰顖氭儊娴狅拷.java缂佹挸鐔�,娑撳秵妲搁惃鍕樈缂佈呯敾鐠嬪啰鏁etAllJavaFilePaths()閺傝纭�
			if (file.isDirectory()) {
 
				getAllJavaFilePaths(file);
 
			} else {
 
				if (file.getName().endsWith(".java")) {
 
					// 娴狀檷BK閺嶇厧绱�,鐠囪褰囬弬鍥︽
					FileInputStream fis = new FileInputStream(file);
					InputStreamReader isr = new InputStreamReader(fis, "GBK");
					BufferedReader br = new BufferedReader(isr);
					String str = null;
 
					// 閸掓稑缂揝tringBuffer鐎涙顑佹稉鑼处鐎涙ê灏�
					StringBuffer sb = new StringBuffer();
 
					// 闁俺绻價eadLine()閺傝纭堕柆宥呭坊鐠囪褰囬弬鍥︽
					while ((str = br.readLine()) != null) {
						// 娴ｈ法鏁eadLine()閺傝纭堕弮鐘崇《鏉╂稖顢戦幑銏ｎ攽,闂囷拷鐟曚焦澧滈崝銊ユ躬閸樼喐婀版潏鎾冲毉閻ㄥ嫬鐡х粭锔胯閸氬酣娼伴崝锟�"\n"閹达拷"\r"
						str += "\n";
						sb.append(str);
					}
					String str2 = sb.toString();
 
					// 娴狀櫅TF-8閺嶇厧绱￠崘娆忓弳閺傚洣娆�,file.getAbsolutePath()閸楀疇顕氶弬鍥︽閻ㄥ嫮绮风�电鐭惧锟�,false娴狅綀銆冩稉宥堟嫹閸旂姷娲块幒銉洬閻╋拷,true娴狅綀銆冩潻钘夊閺傚洣娆�
					FileOutputStream fos = new FileOutputStream(file.getAbsolutePath(), false);
					OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
					osw.write(str2);
					osw.flush();
					osw.close();
					fos.close();
					br.close();
					isr.close();
					fis.close();
				}
			}
		}
	}


}
