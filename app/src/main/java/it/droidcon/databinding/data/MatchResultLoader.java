package it.droidcon.databinding.data;

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import it.droidcon.databinding.BuildConfig;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

public class MatchResultLoader {
    public static final String DRAWN = "drawn";
    public static final String WON = "won";
    public static final String LOST = "lost";
    private FootballDataService api;

    private GiphyService giphyService;

    public static final String MY_TEAM_NAME = "ACF Fiorentina";

    private MatchResultLoader(FootballDataService api, GiphyService giphyService) {
        this.api = api;
        this.giphyService = giphyService;
    }

    @NonNull
    public static MatchResultLoader singleton() {
        OkHttpClient client = new OkHttpClient();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(interceptor);
        }

        return new MatchResultLoader(
                retrofit(client, "http://api.football-data.org/").create(FootballDataService.class),
                retrofit(client, "http://api.giphy.com/").create(GiphyService.class)
        );
    }

    @NonNull
    private static Retrofit retrofit(OkHttpClient client, String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Observable<MatchResult> load() {
        return api.getMatches()
                .flatMapIterable(ApiResponse::getFixtures)
                .filter(f -> "FINISHED".equals(f.getStatus()))
                .last()
                .flatMap(f -> giphyService.randomGif(getSearchTag(f)).map(GiphyResponse::getData).map(GiphyData::getUrl).map(url -> new MatchResult(f, url)));
    }

    private String getSearchTag(Fixture f) {
        Result result = f.getResult();
        int goalsHomeTeam = result.getGoalsHomeTeam();
        int goalsAwayTeam = result.getGoalsAwayTeam();
        if (goalsHomeTeam == goalsAwayTeam) {
            return DRAWN;
        } else {
            if (MY_TEAM_NAME.equals(f.getHomeTeamName())) {
                return goalsHomeTeam > goalsAwayTeam ? WON : LOST;
            } else {
                return goalsHomeTeam < goalsAwayTeam ? WON : LOST;
            }
        }
    }
}
