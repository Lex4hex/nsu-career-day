import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject, Observable, timer } from 'rxjs';
import { takeUntil, map, flatMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  private url = 'http://localhost:8034/';
  public isOpened = false;
  public isArrows = false;
  public round = 2;
  public firstNodeValue = null;
  public firstNodeActive = null;
  public firstNodeStatuses = null;
  public secondNodeValue = null;
  public secondNodeActive = null;
  public secondNodeStatuses = null;
  public thirdNodeValue = null;
  public thirdNodeActive = null;
  public thirdNodeStatuses = null;
  public winner = null;
  public allParticipantNumbers: any[] = null;
  public isLotteryStarts = null;
  public closeReg: Subject<string> = new Subject();
  public dev = true;
  public title = 'nsu-project';
  constructor(private http: HttpClient) {
    this.round = this.getRoundFromLs();
    const body = {};
    this.http.post(`${this.url}openRegistration`, body).subscribe(
      res => {},
      err => {
        if (err.error.message.includes('is already in progress')) {
          this.isOpened = true;
          this.switchRounds();
        }
      }
    );
    this.dev = !environment.production;
  }
  private switchRounds(): void {
    switch (this.round) {
      case 1:
        this.askForFirstNode();
        break;
      case 2:
        this.askForFirstNode();
        this.askForSecondNode();
        break;
      case 3:
        this.askForFirstNodeStatuses();
        this.askForSecondNodeStatuses();
        this.askForThirdNodeStatuses();
        this.getAllNumbers();
        break;
      case 4:
        this.askForFirstNodeStatuses();
        this.askForSecondNodeStatuses();
        this.askForThirdNodeStatuses();
        this.getAllNumbers();
        setTimeout(() => {
          this.closeReg.next('closeSubscription');
          this.isOpened = false;
          this.isLotteryStarts = true;
        }, 3000);
        break;
      case 5:
        this.startLottery();
        break;
    }
  }
  public openRegistration(): void {
    const body = {};
    this.http.post(`${this.url}openRegistration`, body).subscribe(
      res => {
        this.closeReg.next();
        this.isOpened = true;
        this.switchRounds();
      },
      err => {
        if (err.error.message.includes('is already in progress')) {
          this.closeReg.next();
          this.isOpened = true;
          this.switchRounds();
        }
      }
    );
  }
  public nextStep(): void {
    const body = {};
    this.http.post(`${this.url}closeRegistration`, body).subscribe(() => {
      this.setRoundToLs();
      this.closeReg.next('closeSubscription');
      this.isOpened = false;
    });
  }
  public startLottery(): void {
    this.round = 5;
    window.localStorage.removeItem('round');
    window.localStorage.setItem('round', this.round.toString());
    // this.winner = Math.round(Math.random() * 100);
    this.http.get(`${this.url}winner`).subscribe(win => {
      this.winner = win;
    });
    this.getAllNumbers();
  }
  public closeGettingParticipant(): void {
    const body = {};
    this.http.post(`${this.url}closeRegistration`, body).subscribe(() => {
      this.round = 4;
      window.localStorage.removeItem('round');
      window.localStorage.setItem('round', this.round.toString());
      this.closeReg.next('closeSubscription');
      this.isOpened = false;
      this.isLotteryStarts = true;
    });
  }
  private askForFirstNode(): void {
    this.askForNodes('FIRST').subscribe(res => {
      this.firstNodeValue = res;
    });
    this.askAliveNodes('FIRST').subscribe(res => {
      this.firstNodeActive = res;
    });
  }
  private askForSecondNode(): void {
    this.askForNodes('SECOND').subscribe(res => {
      this.secondNodeValue = res;
    });
    this.askAliveNodes('SECOND').subscribe(res => {
      this.secondNodeActive = res;
    });
  }
  private askForFirstNodeStatuses(): void {
    this.askForNodeStatuses('FIRST').subscribe(res => {
      this.firstNodeStatuses = res;
    });
    this.askAliveNodes('FIRST').subscribe(res => {
      this.firstNodeActive = res;
    });
  }
  private askForSecondNodeStatuses(): void {
    this.askForNodeStatuses('SECOND').subscribe(res => {
      this.secondNodeStatuses = res;
    });
    this.askAliveNodes('SECOND').subscribe(res => {
      this.secondNodeActive = res;
    });
  }
  private askForThirdNodeStatuses(): void {
    this.askForNodeStatuses('THIRD').subscribe(res => {
      this.thirdNodeStatuses = res;
    });
    this.askAliveNodes('THIRD').subscribe(res => {
      this.thirdNodeActive = res;
    });
  }
  private askForNodeStatuses(num: string): Observable<{ owner: number; backup: number }> {
    return timer(0, 2000).pipe(
      flatMap(() => this.http.get<{ owner: number; backup: number }>(`${this.url}node/${num}/status`)),
      takeUntil(this.closeReg)
    );
  }
  private askForNodes(num: string): Observable<any> {
    return timer(0, 2000).pipe(
      flatMap(() => this.http.get(`${this.url}node/${num}/data`)),
      map((res: { hashes: string[] }) => {
        return res.hashes;
      }),
      takeUntil(this.closeReg)
    );
  }
  private askAliveNodes(num: string): Observable<boolean> {
    return timer(0, 2000).pipe(
      flatMap(() => this.http.get<boolean>(`${this.url}node/${num}/isAlive`)),
      takeUntil(this.closeReg)
    );
  }
  private getAllNumbers(): void {
    timer(0, 2000)
      .pipe(
        flatMap(() => this.http.get<{ hashes: string[] }>(`${this.url}allClusterNumbers`)),
        map((res: { hashes: string[] }) => {
          return res.hashes;
        }),
        takeUntil(this.closeReg)
      )
      .subscribe(res => {
        this.allParticipantNumbers = res;
      });
  }
  private getRoundFromLs(): number {
    return +window.localStorage.getItem('round');
  }
  private setRoundToLs(): void {
    if (this.round === 3) {
      return;
    }
    this.round++;
    window.localStorage.removeItem('round');
    window.localStorage.setItem('round', this.round.toString());
  }
  public resetAll(): void {
    this.round = 1;
    window.localStorage.removeItem('round');
    window.localStorage.setItem('round', this.round.toString());
    this.http.post(`${this.url}closeRegistration`, {}).subscribe(() => {
      this.closeReg.next();
      this.isOpened = false;
      this.firstNodeActive = false;
      this.firstNodeValue = null;
      this.allParticipantNumbers = null;
    });
    window.location.reload();
  }
}
