#!/usr/bin/env python
#coding=utf-8


#chmod a+x SwitchResourceChannel.py

import os
import string
import shutil
import zipfile
import sys



workdir=os.getcwd()
targetPath="/server"
bakPath="/server/bak"
runApps=[ ['server-cdk', 'server-cdk-0.0.1-SNAPSHOT-release.zip','server-cdk-0.0.1-SNAPSHOT'], 
          ['server-cross', 'server-cross-0.0.1-SNAPSHOT-release.zip','server-cross-0.0.1-SNAPSHOT'],
          ['server-game', 'server-game-0.0.1-SNAPSHOT-release.zip','server-game-0.0.1-SNAPSHOT'],
		   ['server-pay', 'server-pay-0.0.1-SNAPSHOT-release.zip','server-pay-0.0.1-SNAPSHOT']
        ]

def unzip_single(src_file, dest_dir, password):
	if password:
		password = password.encode()

	zf = zipfile.ZipFile(src_file)
	try:
		zf.extractall(path=dest_dir, pwd=password)
	except RuntimeError as e:
		print(e)
	zf.close()

def start():
	isExists=os.path.exists(bakPath)
	if not isExists:
		os.makedirs(bakPath)
		print bakPath+' åˆ›å»ºæˆåŠŸ'
	else:
		print bakPath+' ç›®å½•å·²å­˜åœ?'

	for index in range(len(runApps)):
		srcStrPath=workdir+"/"+runApps[index][0]+"/target/"+runApps[index][1]
		needBackFile=targetPath+"/"+runApps[index][1]
		delDir=targetPath+"/"+runApps[index][2]
		ishave=os.path.exists(needBackFile)
		if ishave:
			shutil.copy(needBackFile,targetPath+"/bak")
		isdel=os.path.exists(delDir)
		if isdel:
			shutil.rmtree(delDir)
		targetStrPath=targetPath
		shutil.copy(srcStrPath,targetStrPath)
		print 'name:', srcStrPath
		
	for file in os.listdir(targetPath):
		if file.endswith('.zip'):
			print 'name:',file
			sourceFile = os.path.join(targetPath,  file)
			unzip_single(sourceFile,targetPath,"")
	
def startSelServer(serverName):
	if cmp(serverName,"all"):
		isExists=os.path.exists(bakPath)
		if not isExists:
			os.makedirs(bakPath)
			print bakPath+'新创建'
		else:
			# 
			print bakPath+'已存在'

		for index in range(len(runApps)):
			srcStrPath=workdir+"/"+runApps[index][0]+"/target/"+runApps[index][1]
			needBackFile=targetPath+"/"+runApps[index][1]
			delDir=targetPath+"/"+runApps[index][2]
			ishave=os.path.exists(needBackFile)
			if ishave:
				shutil.copy(needBackFile,targetPath+"/bak")
			isdel=os.path.exists(delDir)
			if isdel:
				shutil.rmtree(delDir)
			targetStrPath=targetPath
			shutil.copy(srcStrPath,targetStrPath)
			print 'name:', srcStrPath
			
		for file in os.listdir(targetPath):
			if file.endswith('.zip'):
				print 'name:',file
				sourceFile = os.path.join(targetPath,  file)
				unzip_single(sourceFile,targetPath,"")
	else:
		for index in range(len(runApps)):
			if cmp(serverName,runApps[index]):
				srcStrPath=workdir+"/"+runApps[index][0]+"/target/"+runApps[index][1]
				needBackFile=targetPath+"/"+runApps[index][1]
				delDir=targetPath+"/"+runApps[index][2]
				ishave=os.path.exists(needBackFile)
				if ishave:
					shutil.copy(needBackFile,targetPath+"/bak")
				isdel=os.path.exists(delDir)
				if isdel:
					shutil.rmtree(delDir)
				targetStrPath=targetPath
				shutil.copy(srcStrPath,targetStrPath)
				print 'name:', srcStrPath
			else:
				print 'name:', srcStrPath
				continue


if __name__=="__main__":
	
    print "name:", sys.argv[0]
    if sys.argv[0]=="":
      start()
    else:	
      startSelServer(sys.argv[0])
