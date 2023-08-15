export type errorType = {
    code: number;
    message: string;
    exceptionType: string;
};

export type WinNumber = {
    time: string;
    date: string;
    prize: number;
    winner: number;
    topNumberList: number[];
    bonus: number;
};

export type Res = {
    code: number;
    message: string;
    data: any;
};