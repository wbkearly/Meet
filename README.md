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
	