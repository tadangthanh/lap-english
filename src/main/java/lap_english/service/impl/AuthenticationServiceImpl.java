package lap_english.service.impl;

import lap_english.dto.request.AuthRequest;
import lap_english.dto.request.LoginGoogleRequest;
import lap_english.dto.response.TokenResponse;
import lap_english.entity.*;
import lap_english.exception.ResourceNotFoundException;
import lap_english.repository.*;
import lap_english.service.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final RoleRepo roleRepo;
    private final CumulativePointRepo cumulativePointRepo;
    private final AccumulateRepo accumulateRepo;
    private final SkillRepo skillRepo;
    private final UserDailyTaskRepo userDailyTaskRepo;
    private final TaskRepo taskRepo;
    private final TitleRepo titleRepo;
    private final DailyTaskRepo dailyTaskRepo;
    private final UserTitleRepo userTitleRepo;

    @Override
    public TokenResponse login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            User user = userRepo.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Tài khoản hoặc mật khẩu không đúng"));
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResourceNotFoundException("Tài khoản hoặc mật khẩu không đúng");
        }
    }

    @Override
    public TokenResponse loginWithGoogle(LoginGoogleRequest loginGoogleRequest) {
        User user = userRepo.findByEmail(loginGoogleRequest.getEmail()).orElse(null);
        if (user == null) {
            user = saveUser(loginGoogleRequest);
        }
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .json(user.getJson())
                .build();
    }

    private User saveUser(LoginGoogleRequest loginGoogleRequest) {
        User user = new User();
        user.setEmail(loginGoogleRequest.getEmail());
        user.setUsername(loginGoogleRequest.getEmail());
        user.setName(loginGoogleRequest.getName());
        user.setJson(loginGoogleRequest.getJson());
        user.setAvatar(loginGoogleRequest.getAvatar());
        Role role = roleRepo.findRoleByName("ROLE_USER").orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.setRole(role);
        user = userRepo.saveAndFlush(user);

        //init cumulative for new user
        CumulativePoint cumulativePoint = initCumulativePointByUser(user);
        userRepo.saveAndFlush(user);

        // init accumulate for new user
        Accumulate accumulate = initAccumulateByUser(user);
        user.setAccumulate(accumulate);
        userRepo.saveAndFlush(user);

        // init skill for new user
        Skill skill = initSkillByUser(user);
        user.setSkill(skill);
        userRepo.saveAndFlush(user);

        // init userDailyTask
        List<UserDailyTask> userDailyTasks = initDailyTaskByUser(user);
        userRepo.saveAndFlush(user);
        // init title
        List<UserTitle> userTitles = initUserTitleByUser(user);
        user = userRepo.saveAndFlush(user);
        generateDailyTask(user);
        return user;
    }

    private void generateDailyTask(User user) {
        // danh sach cac task cua ngay cu
        List<UserDailyTask> userDailyTasksOld = userDailyTaskRepo.findAllByUserId(user.getId());
        List<Long> idsOldDailyTask = userDailyTasksOld.stream().map(userDailyTask -> userDailyTask.getDailyTask().getId()).toList();
        // lay cac task moi 1 cach ngau nhien
        int dailyTaskNum = 3;
        Pageable pageable = PageRequest.of(0, dailyTaskNum);
        List<DailyTask> taskRandom = dailyTaskRepo.findRandomDailyTasks(idsOldDailyTask, pageable);
//        List<UserDailyTask> userDailyTasksNew = new ArrayList<>();
        for (DailyTask dailyTask : taskRandom) {
            // init userDaily Task
            UserDailyTask userDailyTask = new UserDailyTask();
            userDailyTask.setUser(user);
            userDailyTask.setDailyTask(dailyTask);
            userDailyTask.setProgress(0);
            userDailyTask.setRewardClaimed(false);
            userDailyTask = userDailyTaskRepo.saveAndFlush(userDailyTask);
//            userDailyTasksNew.add(userDailyTask);
        }
        // xoa cac task cu
        idsOldDailyTask.forEach(userDailyTaskRepo::deleteAllByDailyTaskId);
    }

    private List<UserTitle> initUserTitleByUser(User user) {
        int titleNum = 3;
        Pageable pageable = PageRequest.of(0, titleNum);
        List<Task> taskRandom = taskRepo.findRandomTasks(pageable);
        List<UserTitle> userTitles = new ArrayList<>();
        for (Task task : taskRandom) {
            Title title = new Title();
            title.setTask(task);
            title.setReward(task.getReward());
            titleRepo.saveAndFlush(title);
            UserTitle userTitle = new UserTitle();
            userTitle.setProgress(0);
            userTitle.setUser(user);
            userTitleRepo.saveAndFlush(userTitle);
            userTitle.setRewardClaimed(false);
        }
        return userTitles;
    }


    private List<UserDailyTask> initDailyTaskByUser(User user) {
        int dailyTaskNum = 3;
        Pageable pageable = PageRequest.of(0, dailyTaskNum);
        List<Task> taskRandom = taskRepo.findRandomTasks(pageable);
        List<UserDailyTask> userDailyTasks = new ArrayList<>();
        for (Task task : taskRandom) {
            //init dailyTask
            DailyTask dailyTask = new DailyTask();
            dailyTask.setTask(task);
            dailyTask.setReward(task.getReward());
            dailyTaskRepo.saveAndFlush(dailyTask);
            // init userDaily Task
            UserDailyTask userDailyTask = new UserDailyTask();
            userDailyTask.setUser(user);
            userDailyTask.setDailyTask(dailyTask);
            userDailyTask.setProgress(0);
            userDailyTask.setRewardClaimed(false);
            userDailyTaskRepo.saveAndFlush(userDailyTask);
            userDailyTasks.add(userDailyTask);
        }
        return userDailyTasks;
    }

    private CumulativePoint initCumulativePointByUser(User user) {
        CumulativePoint cumulativePoint = new CumulativePoint();
        cumulativePoint.setDiamond(0);
        cumulativePoint.setDiamond(0);
        cumulativePoint.setRankPoints(0);
        cumulativePoint.setUser(user);
        cumulativePointRepo.saveAndFlush(cumulativePoint);
        return cumulativePoint;
    }

    private Accumulate initAccumulateByUser(User user) {
        Accumulate accumulate = new Accumulate();
        accumulate.setWords(0);
        accumulate.setSentences(0);
        accumulate.setTitles(0);
        accumulate.setDaysLearned(0);
        return accumulateRepo.saveAndFlush(accumulate);
    }

    private Skill initSkillByUser(User user) {
        Skill skill = new Skill();
        skill.setListening(1);
        skill.setSpeaking(1);
        skill.setReading(1);
        skill.setWriting(1);
        return skillRepo.saveAndFlush(skill);
    }

    @Override
    public void logout(String token) {

    }

    @Override
    public void verifyToken(String token) {
        if (!jwtService.tokenIsValid(token)) {
            throw new ResourceNotFoundException("Token không hợp lệ");
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        String email = this.jwtService.extractUsername(refreshToken);
        if (this.jwtService.tokenIsValid(refreshToken)) {
            User user = this.userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            String newAccessToken = this.jwtService.generateToken(user);
            String newRefreshToken = this.jwtService.generateRefreshToken(user);
            return TokenResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build();
        } else {
            throw new ResourceNotFoundException("Invalid refresh token");
        }
    }
}
