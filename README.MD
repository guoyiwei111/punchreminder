此应用主要功能是提醒那些经常忘打卡的人打卡。

主要原理就是通过各种系统监听以及各种后台保活来保证app再后台运行。然后到点之后提醒你打卡。（如果设置了电子围栏，则早晨到公司会主动提醒你打卡）

打卡一定要用软件生成的快捷方式打卡，这样软件才能知道你是不是已经打过卡，何时打的卡。

主要功能：

1，记录打卡时间，统计公式

2，如果你忘打卡了，会在设置好的时间提醒你打卡。如果你打过了，则不会提示。


使用注意事项：

1，首次启动需要允许监听状态栏（NotificationListenerService）以及允许后台无限制运行

2，允许开机自启动（为了怕你重启手机应用失效）

3，加入系统白名单（一般是按一下查看所有应用，然后点击那个锁，让应用不会被清理程序清理掉）

4，打卡一定要用本应用的生成的快捷方式打卡（右上角有设置，可以生成快捷方式），或在应用内部打卡才会记录时间。（毕竟本程序不是黑客程序）


以上三点有任何疑问自行百度。一定要保证上面3点，这样才能准确提醒。


PS

如果怀疑有恶意代码，请自己把源码下下来检查，自己编译。默认应用在打卡后会到本开源项目中查找最新的工作历以及最新版本，所以第一次打开应用要有网络。

