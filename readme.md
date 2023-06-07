# Java期末專案 - 聲音編輯軟體

## 軟體介紹

### UI及功能

#### 初始畫面
![](https://hackmd.io/_uploads/rkEwsqpUh.png)
初始頁面底下有六個按鍵，這六個按鍵可以彈出各功能的視窗

#### 讀檔
![](https://hackmd.io/_uploads/BJxdo5682.png)
讀檔頁面有四個按鍵
* Open File：會彈出檔案讀取視窗，可以選擇聲音檔案
* Pause：暫停音檔撥放
* Resume：繼續撥放
* Stop：關閉音檔

#### 錄音
![](https://hackmd.io/_uploads/SJ8_jqp82.png)
錄音頁面有四個按鍵
* Start Record：開始錄音
* Stop Record：停止錄音
* Play File：回放錄音結果
* Save File：儲存錄音檔


#### 混音
![](https://hackmd.io/_uploads/S1aujqa8n.png)
混音頁面有三個按鍵
* Select Files：會彈出檔案選擇視窗，可以選擇兩個檔案
* Select Output File：選擇輸出資料夾
* Mix Audio：混音並輸出


#### 音訊裁切/合併
![](https://hackmd.io/_uploads/S1NYoqTL2.png)
* MERGE MUSIC:回彈出視窗選擇兩個要合併的音檔,合併的音檔會出現在專案資料夾
* CUT MUSIC:首先先輸出要音檔切割的開始跟結束時間,然後按右下角的choose file
  button來選擇要切割的音檔,再按右上角CUT MUSIC就完成切割音檔動作且存到專案資料夾裡
* PLAY,PAUSE:可以測試音檔是否被正確的合併或切割,可以選擇合併或切割的音檔來確認
  是否能正常撥放,並有暫停跟繼續撥放的功能(繼續撥放的功能再按下PAUSE button才有)

#### 綜合效果器
![](https://hackmd.io/_uploads/HypYicaLn.png)
* Overdrive：左邊Overdrive按鈕為單獨的效果開關，Gain拉條為增益強度，Level拉條為截波後的震幅
* Compressor：左邊Compressor按鈕為單獨的效果開關，Level拉條為截波後的震幅
* Delay：左邊Delay按鈕為單獨的效果開關，Delay拉條為延遲時間，Feedback為回音強度
* Start：開始從音源讀取並撥放
* Pause：暫停輸入
* Close：關閉輸入
* Record：若輸入選擇Line In會出現Reecord按鍵，可以開始錄音，再按一下會停止錄音並輸出
* Choose Folder：若輸入選擇Line In會出現Choose Folder按鍵，可以選擇錄音輸出路徑
* Save File：若輸入選擇File In會出現Save File按鍵，會儲存效果處理後的音檔，檔案與原檔同路徑，檔案結尾加上_processed
* Choose File：若輸入選擇File In會出現Choose File按鍵，可以選擇輸入檔案
* Input：可以選擇輸入源，Line In為外部音源，File In為檔案輸入
* Status為狀態欄


#### 視波器
![](https://hackmd.io/_uploads/rkbjicTIn.png)
* Trigger Level：調整視波器的靈敏度
* YScale：震幅放大倍率


## 架構圖
![](https://hackmd.io/_uploads/Hy5dth683.png)
橘色為imterface或abstract class<br>
紅色為class<br>
綠色為package<br>

## Demo
<div>
    <iframe width="560" height="315" src="https://www.youtube.com/embed/8MliEoMIrGQ" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>
</div>