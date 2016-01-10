package fightingpit.barrons1100;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterExpandableWordList extends BaseExpandableListAdapter {

    private List<GenericContainer> WordList ;
    private String aTempChild;
    private ArrayList<String> ChildList = new ArrayList<>();
    private LayoutInflater inflater;
    private Activity activity;

    public AdapterExpandableWordList(List<GenericContainer> iWordList, ArrayList<String> iMeaningList) {
        WordList = iWordList;
        this.ChildList = iMeaningList;
    }

    public void setInflater(LayoutInflater inflater, Activity act) {
        this.inflater = inflater;
        activity = act;
    }

    @Override
    public Object getChild(int wordPosition, int meaningPosition) {
        return null;
    }

    @Override
    public long getChildId(int wordPosition, int meaningPosition) {
        return 0;
    }

    @Override
    public View getChildView(int wordPosition, final int meaningPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {
        aTempChild = ChildList.get(wordPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_meanings_all, null);
        }
        TextView text = (TextView) convertView.findViewById(R.id.tv_lwa_meaning);
        text.setText(aTempChild);
        final int aPosition = wordPosition;

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Meaning Clicked. Hide the Meaning
                WordListFragment.hideMeaning(aPosition);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int wordPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int wordPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return WordList.size();
    }

    @Override
    public void onGroupCollapsed(int wordPosition) {
        super.onGroupCollapsed(wordPosition);
    }

    @Override
    public void onGroupExpanded(int wordPosition) {
        super.onGroupExpanded(wordPosition);
    }

    @Override
    public long getGroupId(int wordPosition) {
        return 0;
    }

    private class ViewHolder {
        protected TextView WordView;
        protected ImageView FavImageView;
    }
    @Override
    public View getGroupView(int wordPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_words_all, null);
            holder.WordView = (TextView) convertView.findViewById(R.id.tv_lwa_word);
            holder.FavImageView = (ImageView) convertView.findViewById(R.id.iv_lwa_fav);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set Word Value
        holder.WordView.setText(WordList.get(wordPosition).getWord());

        // Set appropriate Favourite Image
        if(WordList.get(wordPosition).isFavourite()){
            holder.FavImageView.setImageResource(R.drawable.ic_star_black_24dp);
        }else{
            holder.FavImageView.setImageResource(R.drawable.ic_star_outline_black_24dp);
        }
        final int aPostion = wordPosition;

        // GEt ImageView
        final ImageView aFavImageView = (ImageView) convertView.findViewById(R.id.iv_lwa_fav);
        aFavImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ImageViewClicked. Change Icon and Update DB
                if(WordList.get(aPostion).isFavourite()){
                    WordListFragment.updateFavourite(aPostion,WordList.get(aPostion).getWord(),false);
                }else{
                    WordListFragment.updateFavourite(aPostion,WordList.get(aPostion).getWord(),true);
                }

            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int wordPosition, int meaningPosition) {
        return false;
    }

}
