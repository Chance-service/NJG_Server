@echo off
rem 显示部分
@echo 功能：更新配置文件csv的svn目录, 将更新后的csv文件拷贝到服务器的配置文件目录下面, 服务器的配置文件目录为C:\Cygwin\home\www\game\application\config\file
@echo 
@echo 
@echo             注 意 事 项
@echo ************************************************************************
@echo 可以精简并通过计划任务来制定自动执行,运行前请检查下面目录:
@echo.   
@echo     1.svn_bin 为安装TortoiseSVN客户端的可执行程序目录
@echo     2.svn_work 为更新项目文件的目录
@echo.
@echo WIN7或WINVista或WIN2008的用户请用管理员身份运行
@echo 
@echo ************************************************************************

rem 路径变量,请在此处按实际修改,最后面不要带斜杠
@set svn_bin=D:\Program Files\TortoiseSVN\bin
@set svn_work=G:\docment\dataConfig\server
@set server_cfg_file_dir=F:\server-work\project-gameserver\trunk\server-game\src\main\resources\xml

::更新配置文件
"%svn_bin%"\TortoiseProc.exe/command:update /path:"%svn_work%" /notempfile /closeonend:1

::将配置文件拷贝到服务器的配置文件目录
xcopy  %svn_work% %server_cfg_file_dir% /S /E /Y

@echo finish update and copy xml file
pause
