# Indicator-Seekbar 
Seekbar with Indicator and Float text above 
##Preview ![alt text](https://github.com/ngtien137/Indicator-Seekbar/blob/master/images/reviews.png) 
## Getting Started * This library use Kotlin * Add maven in your root build.gradle at the end of repositories:

``` 
allprojects { 
  repositories { 
    ... 
    maven { url 'https://jitpack.io' }
  } 
} 
``` 

* Add the dependency to file build.gradle(Module:app): 

``` 
implementation 'com.github.ngtien137:Indicator-Seekbar:Tag' 

``` 

TAG is the version of library. If you don't know, remove it with + 
** All Attributes 

``` 

<com.luza.indicatorseekbar.IndicatorSeekbar 
  android:padding="8dp" android:id="@+id/seekbar" 
  android:layout_width="match_parent" 
  android:layout_height="80dp" 
  app:is_progress="50" \\Current Progress of seekbar - Giá trị hiện tại 
  app:is_min="0" \\Max Value of seekbar - Giá trị nhỏ nhất 
  app:is_max="200" \\Min Value of seekbar - Giá trị lớn nhất 
  app:is_number_indicator="9" \\number of indicators - Số lượng mốc trên seekbar 
  
  app:is_thumb="@drawable/ic_drawable" \\drawable of thumb (vector*) - ảnh thumb của seekbar 
  app:is_thumb_touch_extra_area="5dp" \\extra area for touching thumb more easy - gia tăng vùng bấm cho thumb 
  app:is_thumb_size="18dp" \\Thumb size - Kích cỡ của thumb 
  
  app:is_indicator="@drawable/indicator_rect" \\drawable of indicator on seekbar (vector*) 
                                                - Ảnh của các mốc trên seekbar 
  app:is_indicator_width="4dp" \\indicator width - Chiều dài của một mốc 
  app:is_indicator_height="8dp" \\indicator height - Chiều cao của một mốc 
  
  app:is_seekbar_height="4dp" \\seekbar height - Chiều cao của seekbar 
  app:is_seekbar_corners="2dp" \\seekbar corners - Bo viền seekbar, thường = 1/2 chiều cao seekbar 
  app:is_seekbar_color="#00f" \\seekbar color - Màu của thanh seekbar 
  app:is_seekbar_progress_color="#0ff" \\seekbar progress height - Màu của thanh progress 
  
  app:is_show_indicator_text="true" \\show indicator text below seekbar 
                                    - Có hiển thị text ở dưới các mốc hay không   
  app:is_text_indicator_size="14sp" \\indicator text size - Kích cỡ của chúng 
  app:is_text_indicator_color="@color/violet" \\indicator text color - Màu của chúng 
  app:is_space_text_indicator_to_bar="4dp" \\space between indicator text and seekbar 
                                            - Khoảng cách giữa chúng và seekbar 
                                            
  app:is_show_progress_value="true" \\show text of progress above seekbar - Có hiển thị giá trị 
                                    hiện tại ở phía trên của thanh seekbar hay không 
  app:is_text_value_size="16sp" \\Progress text value size - Kích cỡ của nó 
  app:is_space_text_value_to_bar="4dp" \\space between progress value text and seekbar 
                                      - Khoảng cách của nó với seekbar 
  app:is_text_value_color="@color/red" \\Color of Progress text value - Màu của nó 
  
  app:layout_constraintBottom_toBottomOf="parent" 
  app:layout_constraintLeft_toLeftOf="parent" 
  app:layout_constraintRight_toRightOf="parent" 
  app:layout_constraintTop_toTopOf="parent" /> 
  
  ``` 
  ** Listener and some function: - Listener - Sự kiện lắng nghe kéo thả seekbar 
  
  ``` 
  
  indicatorSeekbar.setListener(object:IndicatorSeekbar.ISeekbarListener{ //OnSeeking mean you are seeking the view - Đang kéo 
      override fun onSeeking(progress: Int) { 
          Log.e(TAG,"Seeking, Progress: $progress") 
      } 
      //OnStopSeeking means you release the view - Thả tay ra 
      override fun onStopSeeking(progress: Int) { 
          Log.e(TAG,"Stop Seeking, Progress: $progress")
      } 
  }) 
  
  ``` 
  - Some functions: 
  
  ``` 
  \\ Set Progress for seekbar
  indicatorSeekbar.setProgress(edtProgress.text.toString().toInt()) 
  
  \\Set range 
  indicatorSeekbar.setRange(0,100) 
  
  ``` 
  - Note: If you want to know seekbar is changing because of touching or set by code, check property isSeekByUser:
  (Kiểm tra xem seekbar đang bị thay đổi do mình kéo hay là thay đổi trong code) 
  ``` 
  
  if (indicatorSeekbar.isSeekByUser){ 
      //User touch and drag 
  }else{ 
      //Seekbar change by function setProgress() 
  } 
  
  ```
