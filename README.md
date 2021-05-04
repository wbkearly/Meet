# Meet App (学习项目---社交APP)

- 完成App首页框架搭建，完成权限管理。

- 集成Bmob，完成注册登录功能。

- 集成阿里云OSS，实现图片上传功能。

- 完成用户搜索功能。

- 完成我的页面布局文件的编写。

- 封装RecyclerView适配器

	1. 自定义CommonViewHolder、CommonAdapter

		* CommonViewHolder继承自RecyclerView.ViewHolder

		* CommonAdapter继承自RecyclerView.Adapte&lt;CommonViewHolder&gt;

	2. 编写CommonViewHolder

	```java
	publc class CommonViewHolder<T> extends RecyclerView.ViewHolder {

		// itemView
		private View mContentView;

		// itemView的子View集合
		private SparseArray<View> mViews;
		
		public CommonViewHolder(View itemView) {
			super(itemView);
			mContentView = itemView;
			this.mViews = new SparseArray<>();
		}

		public static CommonViewHolder getViewHolder(ViewGroup parent, int layoutId) {
			return new CommonViewHolder(View.inflate(parent.getContext(), layoutId, null));
		}

		/**
		* 提供给外部访问itemView的子view
		*/
		public <T extends View> T getView(int viewId) {
			View view = mViews.get(viewId);
			if (view == null) {
				view = mContentView.findViewById(viewId);
				mViews.put(viewId, view);
			}
			return (T) view;
		}

	}
	```

	3. 编写CommonViewAdapter
	```java
	public class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

		private List<T> mList;

		private OnBindDataListener<T> mOnBindDataListener;

		/**
		* 提供给外部使用的接口
		*/
		public interface OnBindDataListener<T> {

			void onBindViewHolder(T model, CommonViewHolder viewhoder, int type, int position);

			int getLayoutId(int type);
		}

		public CommonAdapter(List<T> list, OnBindDataListener<T> onBindDataListener) {
			mList = list;
			mOnBindDataListener = onBindDataListener;
		}

		public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			int layoutId = mOnBindDataListener.getLayoutId(viewType);
        	return CommonViewHolder.getViewHolder(parent, layoutId);
    	}

		public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
			mOnBindDataListener.onBindViewHolder(mList.get(position), holder, getItemViewType(position), position);
		}

		public int getItemCount() { 
			return mList == null ? 0 : mList.size(); 
		}
	}
	```

	4. 在CommonAdapter中添加多类型 itemview 处理

	```java
	private OnBindMultiTypeDataListener<T> mOnBindMultiTypeDataListener;
	
	public interface OnBindMultiTypeDataListener<T> extends OnBindDataListener<T> {
        int getItemType(int position);
    }

	public CommonAdapter(List<T> list, OnBindMultiTypeDataListener<T> onBindMultiTypeDataListener) {
        mList = list;
        mOnBindDataListener = onBindMultiTypeDataListener;
        mOnBindMultiTypeDataListener = onBindMultiTypeDataListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mOnBindMultiTypeDataListener != null) {
            return mOnBindMultiTypeDataListener.getItemType(position);
        }
        return 0;
    }
	```

	4. 注意事项

		- 在CommonViewHolder中使用SparseArray来保存子view, 相比较与HashMap而言提高了内存效率。
	
* 完善我的页面头部拉伸滑动效果

* SHA1加密

```java
public class SHA1 {

    private static final char[] HEXES = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    /**
     * Java中byte用二进制表示占用8bit
     * 16进制每个字符需要4bit
     * 因此 将每个byte转换成两个相应的16进制字符，
     * 即把byte的高4位和低4位分别转换成相应的16进制字符H和L
     * 并组合起来得到byte转换到16进制字符串的结果同理，
     * 相反的转换也是将两个16进制字符转换成一个byte
     */
    public static String sha1(String data){
        StringBuilder builder = new StringBuilder();
        try{
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] bits = md.digest(data.getBytes());
            for (int bit : bits) {
                builder.append(HEXES[(bit >> 4) & 0x0F]);
                builder.append(HEXES[bit & 0x0F]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return builder.toString();
    }
}
```

* 完成融云IMLib集成。

* 集成LitePal.


