package com.ftn.heisenbugers.gotaxi.services;

import com.ftn.heisenbugers.gotaxi.models.Driver;
import com.ftn.heisenbugers.gotaxi.models.Passenger;
import com.ftn.heisenbugers.gotaxi.models.Ride;
import com.ftn.heisenbugers.gotaxi.models.User;
import com.ftn.heisenbugers.gotaxi.models.dtos.BlockableUserDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.IsBlockedDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.RideHistoryDTO;
import com.ftn.heisenbugers.gotaxi.models.dtos.UserStateDTO;
import com.ftn.heisenbugers.gotaxi.models.enums.RideSort;
import com.ftn.heisenbugers.gotaxi.models.enums.RideStatus;
import com.ftn.heisenbugers.gotaxi.models.enums.UserState;
import com.ftn.heisenbugers.gotaxi.repositories.RideRepository;
import com.ftn.heisenbugers.gotaxi.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public UserService(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    public UserStateDTO getState(UUID userId) {
        User u = userRepository.findUserById(userId);
        if (u instanceof Passenger p) {
            Optional<Ride> rideOpt = rideRepository.findByRoute_User_IdAndStatus(p.getId(), RideStatus.ONGOING);

            return rideOpt.map(ride -> new UserStateDTO(UserState.RIDING, ride.getId()))
                    .orElseGet(() -> new UserStateDTO(UserState.LOOKING));
        } else if (u instanceof Driver d) {
            Optional<Ride> rideOptStart = rideRepository.findByDriverIdAndStatus(d.getId(), RideStatus.ASSIGNED);
            if (rideOptStart.isPresent()) {
                return new UserStateDTO(UserState.STARTING, rideOptStart.get().getId());
            }
            Optional<Ride> rideOpt = rideRepository.findByDriverIdAndStatus(d.getId(), RideStatus.ONGOING);
            return rideOpt.map(ride -> new UserStateDTO(UserState.DRIVING, ride.getId()))
                    .orElseGet(() -> new UserStateDTO(UserState.READY));

        } else {
            return new UserStateDTO();
        }

    }

    public List<RideHistoryDTO> getUserHistory(Passenger passenger, LocalDate startDate, LocalDate endDate,
                                               RideSort sortBy, String direction) {
        List<Ride> rides;
        UUID userId = passenger.getId();

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy.getProperty()).descending()
                : Sort.by(sortBy.getProperty()).ascending();


        if (startDate != null && endDate == null) {
            rides = rideRepository.findByPassengersContainingAndStartedAtAfter(passenger, startDate.atStartOfDay(), sort);
        } else if (startDate == null && endDate != null) {
            rides = rideRepository.findByPassengersContainingAndStartedAtBefore(passenger,
                    endDate.plusDays(1).atStartOfDay(),
                    sort);
        } else if (startDate != null) {
            rides = rideRepository.findByPassengersContainingAndStartedAtBetween(passenger,
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay(),
                    sort);
        } else {
            rides = rideRepository.findByPassengersContaining(passenger, sort);
        }
        List<RideHistoryDTO> rideHistoryDTOS = new ArrayList<>();
        for (Ride r : rides) {
            RideHistoryDTO dto = new RideHistoryDTO();
            PopulateDto(r, dto);
            rideHistoryDTOS.add(dto);
        }

        return rideHistoryDTOS;
        /*if (startDate != null && endDate == null) {
            rides = rideRepository.findByRoute_User_IdAndStartedAtAfter(
                    userId, startDate.atStartOfDay(), sort
            );
        } else if (startDate == null && endDate != null) {
            rides = rideRepository.findByRoute_User_IdAndStartedAtBefore(
                    userId, endDate.plusDays(1).atStartOfDay(), sort
            );
        } else if (startDate != null) {
            rides = rideRepository.findByRoute_User_IdAndStartedAtBetween(
                    userId,
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay(),
                    sort
            );
        } else {
            rides = rideRepository.findByRoute_User_Id(userId, sort);
        }

        List<RideHistoryDTO> result = new ArrayList<>();
        for (Ride r : rides) {
            RideHistoryDTO dto = new RideHistoryDTO();
            PopulateDto(r, dto);
            result.add(dto);
        }
        return result;*/


    }

    public List<BlockableUserDTO> getBlockableUsers(){
        List<User> blockableUsers = userRepository.findAllActivatedPassengersAndDrivers();
        List<BlockableUserDTO> blockableUserDTOS = new ArrayList<>();
        for(User u : blockableUsers){
            blockableUserDTOS.add(new BlockableUserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getProfileImageUrl(), u.isBlocked(), u instanceof Driver ? "Driver" : "Passenger"));
        }

        return blockableUserDTOS;
    }

    public void block(UUID id, String note){
        User user = userRepository.findById(id).get();

        user.setBlocked(true);
        user.setBlockNote(note);
        userRepository.save(user);
    }

    public void unblock(UUID id){
        User user = userRepository.findById(id).get();

        user.setBlocked(false);
        user.setBlockNote(null);
        userRepository.save(user);
    }

    public IsBlockedDTO isBlocked(String email){
        User user = userRepository.findByEmail(email).get();

        return new IsBlockedDTO(user.isBlocked(), user.getBlockNote());
    }

    private static void PopulateDto(Ride r, RideHistoryDTO dto) {
        dto.setRideId(r.getId());
        dto.setCanceled(r.isCanceled());
        dto.setPrice(r.getPrice());
        dto.setEndedAt(r.getEndedAt());
        try {
            dto.setEndAddress(r.getEnd().getAddress());
        } catch (NullPointerException e) {
            dto.setEndAddress("");
        }
        try {
            dto.setStartAddress(r.getStart().getAddress());
        } catch (NullPointerException e) {
            dto.setStartAddress("");
        }
        dto.setPanicTriggered(r.getPanicEvent() != null);
        dto.setCanceledBy(r.getCanceledBy());
        dto.setStartedAt(r.getStartedAt());

    }

}
