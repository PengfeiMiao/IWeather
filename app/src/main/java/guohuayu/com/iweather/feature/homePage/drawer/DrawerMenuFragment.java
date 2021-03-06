package guohuayu.com.iweather.feature.homePage.drawer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import guohuayu.com.iweather.R;
import guohuayu.com.iweather.base.BaseFragment;
import guohuayu.com.iweather.data.db.entities.weatherEntities.Weather;

/**
 * Created by Administrator on 2018/12/17.
 * 用于实现侧滑菜单
 */

public class DrawerMenuFragment extends BaseFragment implements DrawerContract.View{

    @BindView(R.id.iBtn_addCity)
    ImageButton btn_addCity;
    @BindView(R.id.rv_city_manager)
    RecyclerView rv_cityManager;

    private Unbinder unbinder;

    private List<Weather> weatherList;
    private CityManagerAdapter cityManagerAdapter;

    private DrawerContract.Presenter presenter;

    private OnSelectCity onSelectCity;

    public DrawerMenuFragment(){

    }

    public static DrawerMenuFragment newInstance(){
        return new DrawerMenuFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSelectCity){
            onSelectCity = (OnSelectCity) context;
        }else{
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drawer_menu, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        Context context = rootView.getContext();

        rv_cityManager.setLayoutManager(new GridLayoutManager(context,3));
        weatherList = new ArrayList<>();
        cityManagerAdapter = new CityManagerAdapter(weatherList);
        cityManagerAdapter.setOnItemClickListener(new CityManagerAdapter.OnCityManagerItemClickListener() {
            //点击删除按钮的回调事件
            @Override
            public void onDeleteClick(String cityId) {
                presenter.deleteCity(cityId);
            }

            //点击整个item的事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    presenter.saveCurrentCityToPreference(weatherList.get(position).getCityId());
                    onSelectCity.onSelect(weatherList.get(position).getCityId());
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
            }
        });

        rv_cityManager.setAdapter(cityManagerAdapter);

        presenter.subscribe();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解除绑定和订阅
        unbinder.unbind();
        presenter.unSubscribe();
    }

    @Override
    public void setPresenter(DrawerMenuPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displaySavedCities(List<Weather> weatherList) {
        this.weatherList.clear();
        this.weatherList.addAll(weatherList);
        cityManagerAdapter.notifyDataSetChanged();
    }

    /*
    * 用于实现在城市管理列表中选中城市后，将对应城市的天气信息加载到主界面
    * */
    public interface OnSelectCity{
        void onSelect(String cityId);
    }
}
